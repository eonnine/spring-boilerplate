package com.lims.api.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lims.api.common.properties.auth.AuthProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableConfigurationProperties(value = AuthProperties.class)
@ActiveProfiles({ "header" })
public class HeaderAuthenticateTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("인증이 성공하면 응답으로 인증 토큰을 발급합니다.")
    @Test
    void authenticateSuccess() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("username", "test");
        param.put("password", "123");

        mvc.perform(post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param))
        )
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("accessToken")))
                .andExpect(content().string(containsString("refreshToken")));
    }

    @DisplayName("인증이 실패하면 401 상태 코드와 메세지로 응답하고 인증 토큰이 발급되지 않습니다.")
    @Test
    void authenticateFail() throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("username", "ERROR");
        param.put("password", "123");

        mvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(param))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("message")))
                .andExpect(content().string(not(containsString("accessToken"))))
                .andExpect(content().string(not(containsString("refreshToken"))));

    }

}