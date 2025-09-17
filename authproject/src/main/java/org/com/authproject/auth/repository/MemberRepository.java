package org.com.authproject.auth.repository;

import java.util.Optional;
import java.util.UUID;
import org.com.authproject.auth.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    // 조회 전용은 EntityGraph/전용 쿼리로 N + 1을 방지
    @EntityGraph
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

}
