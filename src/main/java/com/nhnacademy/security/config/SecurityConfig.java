package com.nhnacademy.security.config;

import com.nhnacademy.security.handler.LoginSuccessHandler;
import com.nhnacademy.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@EnableWebSecurity(debug = true)
@Configuration
public class SecurityConfig {


    //TODO 06 웹 요청 ACL 스프링 표현식 적용하기
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/private-project/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MEMBER")
                .antMatchers("/project/**").authenticated()
                .antMatchers("/redirect-index").authenticated()
                .antMatchers("/git/oauth/*").permitAll()
                .antMatchers("/login/oauth2/").permitAll()
                .anyRequest().permitAll()
                .and()
                    .formLogin()
                        .usernameParameter("id")
                        .passwordParameter("pwd")
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
//                .and()
//                    .oauth2Login()
//                        .clientRegistrationRepository(clientRegistrationRepository())
//                        .authorizedClientService(authorizedClientService())
                .and()
                    .logout()
                .and()
                //TODO 07 CSRF Filter 설정
                    .csrf()
//                .disable()
                .and()
                    .sessionManagement()
                        .sessionFixation()
                            .none()
                .and()
                //TODO 05 Security Http Response Header 설정
                    .headers()
                        .defaultsDisabled()
                        .frameOptions().sameOrigin()
                .and()
                    .exceptionHandling()
                        .accessDeniedPage("/error/403")
                .and()
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailsService customUserDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //    TODO 10 Redis template
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler(RedisTemplate<String, String> redisTemplate) {
        return new LoginSuccessHandler(redisTemplate);
    }

    // TODO 11 github Oauth application 정보
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(){
        return new InMemoryClientRegistrationRepository(github());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(){
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    private ClientRegistration github() {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .userNameAttributeName("name")
                .clientId("2f3f0b35e5879ce2bb26")
                .clientSecret("2e945c7195931a4e3f2c1451e94b250ca8bac698")
                .build();
    }
}
