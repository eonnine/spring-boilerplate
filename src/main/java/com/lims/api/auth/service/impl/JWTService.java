package com.lims.api.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.lims.api.auth.service.TokenService;
import com.lims.api.common.dto.ValidationResult;
import com.lims.api.common.properties.auth.AuthProperties;
import com.lims.api.common.properties.auth.TokenProperties;

import java.util.Date;

public class JWTService implements TokenService {

    private final String issuer;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JWTService(TokenProperties tokenProperties) {
        Algorithm algorithm = Algorithm.HMAC256(tokenProperties.getSecret());
        String issuer = tokenProperties.getIssuer();
        this.algorithm = algorithm;
        this.issuer = issuer;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    @Override
    public String createToken(Date expiresAt) {
        return null;
    }

    @Override
    public ValidationResult verifyToken(String token) {
        return null;
    }
}