package com.nhnacademy.security.entity;

import com.nhnacademy.security.service.MemberService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Authorities")
public class Authority {

    @Id
    @Column(name = "member_id")
    private String memberId;

    @Column(name = "authority")
    private String authority;

    @MapsId
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
