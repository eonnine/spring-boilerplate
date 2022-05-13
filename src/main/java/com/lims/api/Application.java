package com.lims.api;

import com.lims.api.auth.domain.AuthJwtProperties;
import com.lims.api.auth.domain.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthJwtProperties.class})
public class Application {

    public static void main(String[] args) { SpringApplication.run(Application.class, args); }

}