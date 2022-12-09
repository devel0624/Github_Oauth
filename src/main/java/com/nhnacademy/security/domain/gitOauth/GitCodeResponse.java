package com.nhnacademy.security.domain.gitOauth;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GitCodeResponse {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
}