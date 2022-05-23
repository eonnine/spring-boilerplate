package com.lims.api.common.config;

import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.auth.service.impl.AuthenticationFactory;
import com.lims.api.common.properties.AuthProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class BeanConfig {

    private AuthenticationFactory authenticationFactory;

    public BeanConfig(AuthProperties authProperties, TokenProvider tokenProvider) {
        this.authenticationFactory = new AuthenticationFactory(authProperties, tokenProvider);
    }

    @Bean
    public TokenAuthenticationProvider tokenAuthenticationProvider() {
        return authenticationFactory.createTokenAuthenticationProvider();
    }

    @Bean
    public TokenHttpHelper tokenHttpHelper() {
        return authenticationFactory.createTokenHttpHelper();
    }

}
