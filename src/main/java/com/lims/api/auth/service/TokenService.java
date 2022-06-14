package com.lims.api.auth.service;

import com.lims.api.auth.domain.Token;

import java.util.Date;

public interface TokenService {

    Token createToken(Date expiresAt);

    boolean verify(Token token);

}