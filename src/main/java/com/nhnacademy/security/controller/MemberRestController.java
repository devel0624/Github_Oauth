package com.nhnacademy.security.controller;

import com.nhnacademy.security.domain.MemberId;
import com.nhnacademy.security.domain.MemberRegisterRequest;
import com.nhnacademy.security.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

//TODO 01 멤버 등록 REST API 개발

@RestController
@RequestMapping("/members")
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberId registerMember(@RequestBody MemberRegisterRequest request){
       return memberService.registerMember(request);
    }
}
