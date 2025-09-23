package org.com.ssrboard.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.BatchSize
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
class Post protected constructor(
    @Column(nullable = false, length = 200)
    var title: String = "",

    @Lob
    @Column(nullable = false)
    var content: String = "",

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "author_id", nullable = false)
    val author: Member
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _comments: MutableList<Comment> = mutableListOf()
    val comments: List<Comment> get() = _comments.toList()

    @CreatedDate
    lateinit var createdAt: LocalDateTime
        private set

    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime
        private set

    fun addComment(comment: Comment) {
        _comments.add(comment)
        comment.bindPost(this)
    }

    fun removeComment(comment: Comment) {
        _comments.remove(comment)
        comment.unbindPost()
    }

    // dirty-checking
    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    companion object {
        fun create(title: String, content: String, author: Member) = Post(title, content, author)
    }

}