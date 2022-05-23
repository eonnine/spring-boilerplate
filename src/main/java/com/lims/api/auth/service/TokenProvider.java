package com.lims.api.auth.service;

import com.lims.api.common.dto.ValidationResult;

import java.util.Date;

public interface TokenProvider {

    public String createToken(Date expiresAt);

    public ValidationResult verifyToken(String token);

}
