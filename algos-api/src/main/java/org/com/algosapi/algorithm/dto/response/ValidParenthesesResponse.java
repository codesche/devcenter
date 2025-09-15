package org.com.algosapi.algorithm.dto.response;

public class ValidParenthesesResponse {

    private final boolean valid;

    public ValidParenthesesResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

}
