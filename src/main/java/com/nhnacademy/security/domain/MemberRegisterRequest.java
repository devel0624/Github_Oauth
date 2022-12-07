package com.nhnacademy.security.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRegisterRequest {

    private String id;
    private String name;
    private String pwd;
    private String authority;
}
