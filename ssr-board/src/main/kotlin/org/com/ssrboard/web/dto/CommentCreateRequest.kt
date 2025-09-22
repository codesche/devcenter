package org.com.ssrboard.web.dto

import jakarta.validation.constraints.NotBlank

class CommentCreateRequest (

    @field:NotBlank
    val content: String,

    @field:NotBlank
    val nickname: String

)