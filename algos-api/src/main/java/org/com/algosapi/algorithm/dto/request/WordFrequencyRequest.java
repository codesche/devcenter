package org.com.algosapi.algorithm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class WordFrequencyRequest {

    @NotBlank(message = "text must not be blank")
    private String text;

    public WordFrequencyRequest() {

    }

    public WordFrequencyRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
