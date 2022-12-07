package com.nhnacademy.security.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

//TODO 04 UserDetails interface 추가하기

@Getter
@Setter
@Entity
@Table(name = "Members")
public class Member{

    @Id
    @Column(name = "member_id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "pwd")
    private String pwd;

    @OneToOne(mappedBy = "member",cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Authority authority;
}
