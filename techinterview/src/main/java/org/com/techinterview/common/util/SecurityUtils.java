package org.com.techinterview.common.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 보안 관련 유틸리티
 */
public class SecurityUtils {

    private static final SecureRandom secureRandom = new SecureRandom();

    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 안전한 랜덤 토큰 생성
     */
    public static String generateSecureRandomToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * 문자열 마스킹 처리
     */
    public static String maskString(String input, int visibleChars) {
        if (input == null || input.length() <= visibleChars) {
            return input;
        }

        StringBuilder masked = new StringBuilder(input.substring(0, visibleChars));
        masked.append("*".repeat(input.length() - visibleChars));

        return masked.toString();
    }

}
