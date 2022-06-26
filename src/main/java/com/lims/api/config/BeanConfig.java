package com.lims.api.config;

import com.lims.api.config.xss.XssJackson2HttpMessageConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Slf4j
@Configuration
public class BeanConfig {

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new XssJackson2HttpMessageConverter();
    }

}