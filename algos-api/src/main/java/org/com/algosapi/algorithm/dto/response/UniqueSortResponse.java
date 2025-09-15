package org.com.algosapi.algorithm.dto.response;

public class UniqueSortResponse {

    private final int[] numbers;

    public UniqueSortResponse(int[] numbers) {
        this.numbers = numbers;
    }

    public int[] getNumbers() {
        return numbers;
    }

}
