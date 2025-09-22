package org.com.ssrboard.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Comment protected constructor(
    @Column(nullable = false, length = 1000)
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    val author: Member
) {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private var post: Post? = null

    fun bindPost(p: Post) {
        this.post = p
    }

    fun unbindPost() {
        this.post = null
    }

    @CreatedDate
    lateinit var createdAt: LocalDateTime
        private set

    companion object {
        fun create(content: String, author: Member) = Comment(content, author)
    }

}