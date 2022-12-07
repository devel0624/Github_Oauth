package com.nhnacademy.security.config;

import com.nhnacademy.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


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
                .anyRequest().permitAll()
                .and()
                    .formLogin()
                        .usernameParameter("id")
                        .passwordParameter("pwd")
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/login")
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
}
