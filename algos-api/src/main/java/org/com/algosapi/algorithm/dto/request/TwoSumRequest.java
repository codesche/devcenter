package org.com.algosapi.algorithm.dto.request;

import jakarta.validation.constraints.NotNull;

public class TwoSumRequest {

    @NotNull
    private int[] numbers;

    @NotNull
    private Integer target;

    public TwoSumRequest() {}

    public TwoSumRequest(int[] numbers, Integer target) {
        this.numbers = numbers;
        this.target = target;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public Integer getTarget() {
        return target;
    }

}
