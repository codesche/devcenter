package org.com.authproject.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 엔티티
 * - 비밀번호는 해시로 저장
 * - 엔티티는 외부 파라미터로 직접 사용하지 않음
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "members", indexes = {
    @Index(name = "idx_member_username", columnList = "username", unique = true)
})
public class Member extends BaseTime {

    @Id
    @GeneratedValue
    @Column
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 60)
    private String passwordHash;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Builder
    private Member(UUID id, String username, String passwordHash, String nickname) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
    }

    /** 비밀번호 변경(더티 체킹) */
    public void changePasswordHash(String newHash) {
        this.passwordHash = newHash;
    }

}
