package com.lims.api.config;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.service.AuthTokenProvider;
import com.lims.api.auth.service.impl.AuthTokenProviderFactoryBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FactoryBeanConfig {

    private final AuthProperties authProperties;

    @Bean
    public FactoryBean<AuthTokenProvider> authTokenProvider() {
        return new AuthTokenProviderFactoryBean(authProperties);
    }

}