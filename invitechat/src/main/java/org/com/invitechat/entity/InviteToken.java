package org.com.invitechat.entity;

import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 초대 토큰 문서
 * - TTL 인덱스를 이용해 만료 시 MongoDB가 자동 삭제(운영 편의성↑)
 * - token 필드는 unique로 보장
 */

@Document(collection = "invite_tokens")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InviteToken {

    @Id
    private String id;              // UUIDv7 문자열 등

    @Indexed(unique = true)
    private String token;           // 초대 토큰(이번 과제에서는 UUID 기반 문자열 사용)

    @Indexed
    private String roomId;          // 대상 채팅방 ID

    private String createdByMemberId;           // 생성자(Member PK)

    /**
     * expireAfterSeconds=0 + expiresAt 필드에 실제 만료 시각을 저장하면,
     * 만료된 문서는 MongoDB가 자동으로 정리한다.
     */
    @Indexed(expireAfter = "0")
    private Instant expiresAt;

    private boolean consumed;

}
