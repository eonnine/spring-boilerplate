package com.lims.api.common.config;

import com.lims.api.auth.service.TokenAuthenticationProvider;
import com.lims.api.auth.service.TokenHttpHelper;
import com.lims.api.auth.service.TokenProvider;
import com.lims.api.auth.service.impl.AuthenticationFactory;
import com.lims.api.common.properties.auth.AuthProperties;
import com.lims.api.common.util.xss.XssJackson2HttpMessageConverter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

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

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new XssJackson2HttpMessageConverter();
    }

}