package org.com.algosapi.algorithm.service;

import static org.junit.jupiter.api.Assertions.*;

import org.com.algosapi.algorithm.dto.request.ReverseStringRequest;
import org.com.algosapi.algorithm.dto.request.TwoSumRequest;
import org.com.algosapi.algorithm.dto.request.UniqueSortRequest;
import org.com.algosapi.algorithm.dto.request.ValidParenthesesRequest;
import org.com.algosapi.algorithm.dto.request.WordFrequencyRequest;
import org.com.algosapi.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AlgorithmServiceImplTest {

    private final AlgorithmServiceImpl service = new AlgorithmServiceImpl();

    @Test
    void reverse_ok() {
        var res = service.reverse(new ReverseStringRequest("abcd"));
        assertEquals("dcba", res.getReversed());
    }

    @Test
    void word_frequency_ok() {
        var res = service.wordFrequency(new WordFrequencyRequest("Hello, hello world!"));
        assertEquals(2, res.getFrequencies().get(0).getCount());
    }

    @Test
    void two_sum_ok() {
        var res = service.twoSum(new TwoSumRequest(new int[]{2, 7, 11, 15}, 9));
        assertEquals(0, res.getIndex1());
        assertEquals(1, res.getIndex2());
    }

    @Test
    void two_sum_not_found() {
        assertThrows(DomainException.class, () ->
            service.twoSum(new TwoSumRequest(new int[]{1, 2, 3}, 100))
        );
    }

    @Test
    void unique_sort_ok() {
        var res = service.uniqueSort(new UniqueSortRequest(new int[]{3, 1, 2, 3, 2}));
        assertArrayEquals(new int[]{1, 2, 3}, res.getNumbers());
    }

    @Test
    void valid_parentheses_ok() {
        var res = service.validParentheses(new ValidParenthesesRequest("([]){}"));
        assertTrue(res.isValid());
    }

}























