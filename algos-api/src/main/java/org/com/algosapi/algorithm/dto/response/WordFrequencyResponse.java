package org.com.algosapi.algorithm.dto.response;

import java.util.List;
import org.com.algosapi.algorithm.entity.WordCount;

public class WordFrequencyResponse {

    private final List<WordCount> frequencies;

    public WordFrequencyResponse(List<WordCount> frequencies) {
        this.frequencies = frequencies;
    }

    public List<WordCount> getFrequencies() {
        return frequencies;
    }

}
