package com.hospital.common.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 공통 시간 정보를 관리하는 기본 엔티티 클래스
 * 모든 엔티티는 이 클래스를 상속받아 생성일시와 수정일시를 자동으로 관리
 * Instant 클래스를 사용하여 UTC 기준 시간으로 처리하며 확장성을 고려
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTime {

    /**
     * 엔티티 생성 시간 (UTC 기준)
     * 한번 설정되면 변경되지 않음 (updatable = false)
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 엔티티 최종 수정 시간 (UTC 기준)
     * 엔티티가 변경될 때마다 자동으로 업데이트 됨
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

}
