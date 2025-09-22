package org.com.ssrboard.web.dto

import org.com.ssrboard.domain.Post

class PostResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorNickname: String,
    val commentCount: Int
) {
    companion object {
        fun from(post: Post) = PostResponse(
            id = post.id!!,
            title = post.title,
            content = post.content,
            authorNickname = post.author.nickname,
            commentCount = post.comments.size
        )
    }
}