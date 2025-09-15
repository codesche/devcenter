package org.com.algosapi.algorithm.service;

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
import org.springframework.transaction.annotation.Transactional;

public interface AlgorithmService {

    @Transactional(readOnly = true)
    ReverseStringResponse reverse(ReverseStringRequest request);

    @Transactional(readOnly = true)
    WordFrequencyResponse wordFrequency(WordFrequencyRequest request);

    @Transactional(readOnly = true)
    TwoSumResponse twoSum(TwoSumRequest request);

    @Transactional(readOnly = true)
    UniqueSortResponse uniqueSort(UniqueSortRequest request);

    @Transactional(readOnly = true)
    ValidParenthesesResponse validParentheses(ValidParenthesesRequest request);

}
