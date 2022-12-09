package com.nhnacademy.security.exception;

public class GitOauthAccessDeniedException extends RuntimeException {
    public GitOauthAccessDeniedException() {
        super("Git Oauth Access Denied");
    }
}
