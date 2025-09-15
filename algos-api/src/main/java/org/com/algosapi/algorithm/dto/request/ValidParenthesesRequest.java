package org.com.algosapi.algorithm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ValidParenthesesRequest {

    @NotBlank
    private String str;

    public ValidParenthesesRequest() {}

    public ValidParenthesesRequest(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

}
