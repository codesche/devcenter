package org.com.authproject.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.annotation.Resources;
import org.com.authproject.auth.dto.request.LoginRequest;
import org.com.authproject.auth.dto.response.TokenResponse;
import org.com.authproject.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Resource
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인 성공 시 액세스/리프레시 토큰을 반환한다.")
    void login_success() throws Exception {
        Mockito.when(authService.login(any(LoginRequest.class)))
            .thenReturn(TokenResponse.builder()
                .accessToken("access.jwt.token")
                .refreshToken("refresh.jwt.token")
                .build());

        LoginRequest req = LoginRequest.builder()
            .username("tester")
            .password("josdf5675s!23")
            .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("access.jwt.token"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh.jwt.token"));
    }

}