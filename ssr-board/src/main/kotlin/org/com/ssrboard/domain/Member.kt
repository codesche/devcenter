package org.com.ssrboard.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Member protected constructor(
    @Column(nullable = false, unique = true, length = 50)
    var nickname: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    lateinit var createdAt: LocalDateTime
        private set

    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime
        private set

    companion object {
        fun create(nickname: String) = Member(nickname)
    }
}