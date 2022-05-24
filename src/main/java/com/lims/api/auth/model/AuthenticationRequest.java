package com.lims.api.auth.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class AuthenticationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Integer num;

    private AuthenticationRequest child;

    private List<String> list;

    private boolean veri;

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}