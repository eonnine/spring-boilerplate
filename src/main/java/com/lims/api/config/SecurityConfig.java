package com.lims.api.config;

import com.lims.api.auth.domain.AuthProperties;
import com.lims.api.auth.service.impl.AuthJWTProvider;
import com.lims.api.filter.AuthCheckFilter;
import com.lims.api.i18n.service.LocaleMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthCheckFilter authCheckFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .csrf()
                .disable()
            .httpBasic()
                .disable()
            .formLogin()
                .disable()
            .cors()
        .and()
            .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.DENY))
        .and()
            .authorizeRequests()
                .anyRequest().permitAll()
        .and()
            .addFilterBefore(authCheckFilter, BasicAuthenticationFilter.class);
    }
}