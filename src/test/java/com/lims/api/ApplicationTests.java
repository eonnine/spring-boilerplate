package com.lims.api;

import com.lims.api.common.properties.auth.AuthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableConfigurationProperties(value = AuthProperties.class)
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}