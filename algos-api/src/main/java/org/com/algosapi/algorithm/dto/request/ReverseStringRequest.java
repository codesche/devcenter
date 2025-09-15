package org.com.algosapi.algorithm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReverseStringRequest {

    @NotBlank(message = "text must not be blank")
    private String text;

    public ReverseStringRequest() {}

    public ReverseStringRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
