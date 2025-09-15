package org.com.invitechat.security;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 인증 완료 후 SecurityContext에 올라가는 사용자 정보.
 * - 면접/실무에서 흔히 UserDetails를 구현해 Principal로 사용한다.
 * - 여기서는 memberId(문자열, UUID/숫자 등)를 명확히 보관해서
 *   비즈니스 로직에서 사용할 수 있도록 한다.
 */

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {
    private final String id;                    // <- 실제 Member PK (예: UUIDv7 문자열)
    private final String username;              // 로그인 계정명
    private final String password;              // 해시된 비밀번호(실제 반환은 필요 없지만 규격상 필드 유지)
    private final List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
