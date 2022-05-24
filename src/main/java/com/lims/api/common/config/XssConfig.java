package com.lims.api.common.config;

import com.lims.api.common.util.xss.XssJackMessageConverter;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class XssConfig {

    @Bean
    public FilterRegistrationBean<XssEscapeServletFilter> xssEscapeServletFilter() {
        FilterRegistrationBean<XssEscapeServletFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new XssEscapeServletFilter());
        filter.setOrder(1);
        filter.addUrlPatterns("/*");
        return filter;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new XssJackMessageConverter();
    }

}