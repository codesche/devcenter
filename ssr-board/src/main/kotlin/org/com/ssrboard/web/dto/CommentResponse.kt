package org.com.ssrboard.web.dto

import org.com.ssrboard.domain.Comment

data class CommentResponse(val authorNickname: String, val content: String)
data class PostDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val authorNickname: String,
    val comments: List<CommentResponse>
)