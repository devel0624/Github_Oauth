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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
    public String gitOauth() throws IOException {

        /**
         * TODO 1. 사용자의 GitHub ID 요청
         * GET https://github.com/login/oauth/authorize
         *
         * 매개변수
         *
         * client_id	string	필수, 등록 할 때 GitHub에서 받은 클라이언트 ID 입니다.
         * redirect_uri	string	승인 후 사용자가 전송되는 애플리케이션의 URL입니다. 리디렉션 URL 에 대한 자세한 내용은 아래를 참조하세요 .
         * login	    string	로그인 및 앱 인증에 사용할 특정 계정을 제안합니다.
         * scope	    string	공백으로 구분된 범위 목록입니다 . 제공되지 않은 경우 scope 애플리케이션에 대한 범위를 인증하지 않은 사용자에 대해 기본적으로 빈 목록이 사용됩니다. 애플리케이션에 대해 승인된 범위가 있는 사용자의 경우 범위 목록이 있는 OAuth 승인 페이지가 사용자에게 표시되지 않습니다. 대신 흐름의 이 단계는 사용자가 애플리케이션에 대해 권한을 부여한 범위 집합으로 자동으로 완료됩니다. 예를 들어 사용자가 이미 웹 흐름을 두 번 수행했고 범위가 있는 토큰 하나와 user범위가 있는 다른 토큰 을 인증한 repo경우 a를 제공하지 않는 세 번째 웹 흐름은 및 범위 scope가 있는 토큰을 받습니다 .userrepo
         * state	    string	추측할 수 없는 임의의 문자열입니다. 교차 사이트 요청 위조 공격으로부터 보호하는 데 사용됩니다.
         * allow_signup	string	인증되지 않은 사용자에게 OAuth 흐름 중에 GitHub에 가입할 수 있는 옵션이 제공되는지 여부입니다. 기본값은 true입니다. false정책에서 가입을 금지하는 경우 사용 합니다.
         */

        log.info("Get Code");

        return REDIRECT + GIT_OAUTH_URL + "?client_id=" + CLIENT_ID + "&scope=user:email";
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

            /**
             * TODO 2. 사용자는 GitHub에 의해 사이트로 다시 리디렉션됩니다.
             * github.com/login/oauth/access_token
             *
             * 매개변수
             *
             * client_id	    string	필수, OAuth 앱용 GitHub에서 받은 클라이언트 ID입니다.
             * client_secret	string	필수, OAuth 앱에 대해 GitHub에서 받은 클라이언트 암호입니다.
             * code	            string	필수, 1단계에 대한 응답으로 받은 코드.
             * redirect_uri	    string	승인 후 사용자가 전송되는 애플리케이션의 URL입니다.
             */

            GitCodeResponse gitCodeResponse = restTemplate.postForEntity(GIT_ACCESS_TOKEN,param,GitCodeResponse.class).getBody();

            assert gitCodeResponse != null;
            log.info("##############################################");
            log.info("Responses");
            log.info("access_token : " + gitCodeResponse.getAccess_token());
            log.info("scope : " + gitCodeResponse.getScope());
            log.info("token_type : " + gitCodeResponse.getToken_type());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer " + gitCodeResponse.getAccess_token());
            HttpEntity entity = new HttpEntity(headers);

            log.info("##############################################");
            log.info("Entity");
            log.info("HttpEntity Header : " + entity.getHeaders());
            log.info("HttpEntity Body : " + entity.getBody());

            /**
             * TODO 3. 액세스 토큰을 사용하여 API에 액세스
             * 액세스 토큰을 사용하면 사용자를 대신하여 API에 요청할 수 있습니다.
             *
             * Authorization: Bearer OAUTH-TOKEN
             * GET https://api.github.com/user
             *
             */

            ResponseEntity<GitEamil> responseEntity = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET,entity,GitEamil.class);

            log.info("##############################################");
            log.info("Access_info");
            log.info("User Email : " + responseEntity.getBody().getEmail());

            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }





    @Getter
    @Setter
    @NoArgsConstructor
    public static class GitCodeResponse {
        private String state;
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

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GitEamil{
        private String email;
    }


}
