package org.com.algosapi.algorithm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.com.algosapi.algorithm.dto.request.ReverseStringRequest;
import org.com.algosapi.algorithm.dto.request.TwoSumRequest;
import org.com.algosapi.algorithm.dto.request.UniqueSortRequest;
import org.com.algosapi.algorithm.dto.request.ValidParenthesesRequest;
import org.com.algosapi.algorithm.dto.request.WordFrequencyRequest;
import org.com.algosapi.algorithm.dto.response.ReverseStringResponse;
import org.com.algosapi.algorithm.dto.response.TwoSumResponse;
import org.com.algosapi.algorithm.dto.response.UniqueSortResponse;
import org.com.algosapi.algorithm.dto.response.ValidParenthesesResponse;
import org.com.algosapi.algorithm.dto.response.WordFrequencyResponse;
import org.com.algosapi.algorithm.service.AlgorithmService;
import org.com.algosapi.common.api.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/algorithms")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    @PostMapping("/reverse")
    public ApiResponse<ReverseStringResponse> reverse(@Valid @RequestBody ReverseStringRequest req) {
        return ApiResponse.ok(algorithmService.reverse(req));
    }

    @PostMapping("/word-frequency")
    public ApiResponse<WordFrequencyResponse> wordFrequency(@Valid @RequestBody WordFrequencyRequest req) {
        return ApiResponse.ok(algorithmService.wordFrequency(req));
    }

    @PostMapping("/two-sum")
    public ApiResponse<TwoSumResponse> twoSum(@Valid @RequestBody TwoSumRequest req) {
        return ApiResponse.ok(algorithmService.twoSum(req));
    }

    @PostMapping("/unique-sort")
    public ApiResponse<UniqueSortResponse> uniqueSort(@Valid @RequestBody UniqueSortRequest req) {
        return ApiResponse.ok(algorithmService.uniqueSort(req));
    }

    @PostMapping("/valid-parentheses")
    public ApiResponse<ValidParenthesesResponse> validParentheses(@Valid @RequestBody ValidParenthesesRequest req) {
        return ApiResponse.ok(algorithmService.validParentheses(req));
    }

}
