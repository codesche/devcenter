package org.com.algosapi.algorithm.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.com.algosapi.algorithm.dto.request.ReverseStringRequest;
import org.com.algosapi.algorithm.dto.response.ReverseStringResponse;
import org.com.algosapi.algorithm.service.AlgorithmService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AlgorithmController.class)
class AlgorithmControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    AlgorithmService service;

    @Test
    void reverse_ok() throws Exception {
        Mockito.when(service.reverse(Mockito.any(ReverseStringRequest.class)))
            .thenReturn(new ReverseStringResponse("dcba"));

        mockMvc.perform(post("/api/v1/algorithms/reverse")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"text\":\"abcd\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.reversed").value("dcba"));
    }

}