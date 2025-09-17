package org.com.authproject.auth.infra.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.com.authproject.auth.common.exception.DomainException;
import org.com.authproject.auth.common.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * AccessToken 검증 필터
 * - Refresh 검증은 서비스 레벨에서 별도 수행
 * - 무상태 서버를 전제로 동작
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            if (jwtProvider.isExpired(token)) {
                throw new DomainException(ErrorCode.AUTH_TOKEN_EXPIRED);
            }
            Jws<Claims> jws = jwtProvider.parse(token);
            String username = jws.getBody().getSubject();

            // 간단한 Username 기반 인증 토큰 생성 (권한은 확장 지점)
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ex) {
            // 실패한 경우 SecurityContext 비움
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}





















