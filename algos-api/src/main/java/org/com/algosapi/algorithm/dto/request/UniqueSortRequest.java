package org.com.algosapi.algorithm.dto.request;

import jakarta.validation.constraints.NotNull;

public class UniqueSortRequest {

    @NotNull
    private int[] numbers;

    public UniqueSortRequest() {}

    public UniqueSortRequest(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return numbers;
    }

}
