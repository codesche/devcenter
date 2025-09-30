package org.com.newsletter.common.swagger;

/**
 * Swagger 문구/태그를 한 곳에서 관리하는 클래스.
 * - 컨트롤러에서는 @Tag(name = ApiDoc.News.TAG) 등 최소한만 사용.
 */
public final class ApiDoc {

    private ApiDoc() { }

    public static final class News {
        private News() { }
        public static final String TAG = "News";
        public static final String SEARCH_SUMMARY = "뉴스 검색 (Redis 캐시 적용)";
        public static final String SEARCH_DESC = "외부 뉴스 API를 호출하여 결과를 반환합니다. "
            + "동일 질의는 TTL 동안 Redis 캐시에서 응답합니다.";
    }

    public static final class Auth {
        private Auth() { }
        public static final String TAG = "Auth";
        public static final String REGISTER_SUMMARY = "회원가입 및 토큰 발급";
        public static final String LOGIN_SUMMARY = "로그인 및 토큰 발급";
        public static final String REFRESH_SUMMARY = "액세스/리프레스 토큰 재발급";
    }

}
