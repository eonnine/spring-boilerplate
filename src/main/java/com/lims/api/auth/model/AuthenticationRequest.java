package com.lims.api.auth.model;

import com.auth0.jwt.interfaces.Claim;
import lombok.Getter;

@Getter
public class AuthenticationRequest {

    private String username;
    private String password;

}