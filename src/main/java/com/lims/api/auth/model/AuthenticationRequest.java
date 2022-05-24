package com.lims.api.auth.model;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class AuthenticationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}