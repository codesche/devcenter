package org.com.ssrboard.web.dto

import org.com.ssrboard.domain.Comment

class CommentResponse (
    val id: Long,
    val content: String,
    val authorNickname: String
) {
    companion object {
        fun from(c: Comment) = CommentResponse(
            id = c.id!!,
            content = c.content,
            authorNickname = c.author.nickname
        )
    }
}