package org.com.ssrboard.repository

import org.com.ssrboard.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<Member, Long> {
    fun findByNickname(nickname: String): Member?
}