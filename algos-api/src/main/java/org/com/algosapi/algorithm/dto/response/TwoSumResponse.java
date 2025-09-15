package org.com.algosapi.algorithm.dto.response;

public class TwoSumResponse {

    private final int index1;
    private final int index2;

    public TwoSumResponse(int index1, int index2) {
        this.index1 = index1;       // 0-based
        this.index2 = index2;
    }

    public int getIndex1() {
        return index1;
    }

    public int getIndex2() {
        return index2;
    }

}
