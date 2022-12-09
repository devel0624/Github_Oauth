package com.nhnacademy.security.controller;

import com.nhnacademy.security.exception.GitOauthAccessDeniedException;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Slf4j
@Controller
@PropertySource("classpath:client.properties")
public class GitOauthController{


    private ClassLoader classLoader;
    @Autowired
    RestTemplate restTemplate;

    private final String REDIRECT = "redirect:";
    private final String GIT_OAUTH_URL = "https://github.com/login/oauth/authorize";
    private final String CLIENT_ID = "2f3f0b35e5879ce2bb26";
    private final String CLIENT_SECRET ="2e945c7195931a4e3f2c1451e94b250ca8bac698";
    private final String GIT_ACCESS_TOKEN = "https://github.com/login/oauth/access_token";

    @GetMapping("/git/oauth/login")
    public String gitOauth(){

        log.info("Get Code");

        return REDIRECT + GIT_OAUTH_URL + "?client_id=" + CLIENT_ID;
    }

    @GetMapping("/login/oauth2/code/github")
    public String getCode(@RequestParam("code") Optional<String> code,
                          @RequestParam("error") Optional<String> error){
        if (error.isPresent()){
            throw new GitOauthAccessDeniedException();
        }else if(code.isPresent()){
            log.info("Receive Code " + code);

            GitCodeParam param = new GitCodeParam();

            param.setClient_id(CLIENT_ID);
            param.setClient_secret(CLIENT_SECRET);
            param.setCode(code.get());

            log.info("Before Get Access Token");

            GitCodeResponse response = restTemplate.postForEntity(GIT_ACCESS_TOKEN,param,GitCodeResponse.class).getBody();

            log.info("response : " + response);

            HttpHeaders headers = new HttpHeaders();
            assert response != null;
            headers.add("Authorization","Bearer " + response.getAccess_token());
            HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange("https://api.github.com/user", HttpMethod.POST,entity,String.class);
            log.info(responseEntity.getBody());
            log.info(String.valueOf(responseEntity.getStatusCode()));
            log.info(String.valueOf(responseEntity.getStatusCodeValue()));


            return "index";
        }else {
            return "index";
        }
    }





    @Getter
    @Setter
    @NoArgsConstructor
    @ToString
    public static class GitCodeResponse {
        private String access_token_secret;
        private String state;
        private String expires_in;
        private String access_token;
        private String refresh_token;
        private String scope;
        private String token_type;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GitCodeParam {
        private String client_id;
        private String client_secret;
        private String code;
    }


}
