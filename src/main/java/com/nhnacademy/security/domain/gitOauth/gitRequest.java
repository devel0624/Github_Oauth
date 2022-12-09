package com.nhnacademy.security.domain.gitOauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class gitRequest{
    String client_id;
    String client_secret;
    String redirect_uri;
    String grant_type;
    String code;
}
