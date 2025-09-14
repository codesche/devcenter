package countingwords;

import java.util.*;
import java.util.stream.*;

// 단어 빈도 수 세기
// 구두점 제거, 소문자 변환, 공백 분리 기준으로 단어 수 세기
public class WordFreqEx1 {

    // 구두점 제거, 소문자 변환, 공백 분리
    public static Map<String, Integer> wordFreq(String text) {
        Map<String, Integer> freq = new HashMap<>();

        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = normalized.trim().split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            Integer cnt = freq.get(token);

            if (cnt == null) {
                cnt = 0;
            }

            freq.put(token, cnt + 1);
        }

        return freq;
    }

    // Stream API
    public static Map<String, Long> wordFreqStream(String text) {
        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        return Arrays.stream(normalized.trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.groupingBy(token -> token, Collectors.counting()));
    }

    public static void main(String[] args) {
        String text = "Hello, hello!! Java & SQL—sql. JAVA?";
        Map<String, Integer> freq1 = wordFreq(text);
        Map<String, Long> freq2 = wordFreqStream(text);

        // 기대(대략): {hello=2, java=2, sql=2}
        System.out.println("[P2] wordFreq: " + freq1);
        System.out.println("[P2] wordFreqStream: " + freq2);
    }

}
