package org.com.algosapi.algorithm.service;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import org.com.algosapi.algorithm.entity.WordCount;
import org.com.algosapi.common.exception.DomainException;
import org.com.algosapi.common.exception.ErrorCode;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final Pattern WORD = Pattern.compile("[a-zA-Z0-9']+");

    @Override
    public ReverseStringResponse reverse(ReverseStringRequest request) {
        var input = request.getText();
        var sb = new StringBuilder(input);
        return new ReverseStringResponse(sb.reverse().toString());
    }

    @Override
    public WordFrequencyResponse wordFrequency(WordFrequencyRequest request) {
        String lower = request.getText().toLowerCase(Locale.ROOT);
        Matcher matcher = WORD.matcher(lower);
        Map<String, Long> map = new HashMap<>();

        while (matcher.find()) {
            map.merge(matcher.group(), 1L, Long::sum);
        }

        List<WordCount> list = map.entrySet().stream()
            .map(e -> new WordCount(e.getKey(), e.getValue()))
            .sorted(Comparator.comparingLong(WordCount::getCount).reversed()
                .thenComparing(WordCount::getWord))
                .collect(Collectors.toList());
        return new WordFrequencyResponse(list);
    }

    @Override
    public TwoSumResponse twoSum(TwoSumRequest request) {
        int[] nums = request.getNumbers();
        int target = request.getTarget();
        Map<Integer, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            Integer j = indexByValue.get(need);
            if (j != null) {
                return new TwoSumResponse(j, i);
            }
            indexByValue.put(nums[i], i);
        }
        throw new DomainException(ErrorCode.NOT_FOUND, "No two-sum solution");
    }

    @Override
    public UniqueSortResponse uniqueSort(UniqueSortRequest request) {
        int[] arr = request.getNumbers();
        return new UniqueSortResponse(
            Arrays.stream(arr).distinct().sorted().toArray()
        );
    }

    @Override
    public ValidParenthesesResponse validParentheses(ValidParenthesesRequest request) {
        String str = request.getStr();
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : str.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return new ValidParenthesesResponse(false);
                }

                char top = stack.pop();
                if (!matches(top, c)) {
                    return new ValidParenthesesResponse(false);
                }
            }
        }

        return new ValidParenthesesResponse(stack.isEmpty());
    }

    private boolean matches(char open, char close) {
        return (open == '(' && close == ')') || (open == '[' && close == ']') || (open == '{' && close == '}');
    }
}
