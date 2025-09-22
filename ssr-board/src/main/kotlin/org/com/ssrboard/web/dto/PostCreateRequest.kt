package org.com.ssrboard.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class PostCreateRequest (

    @field:NotBlank
    @field:Size(max = 200)
    val title: String,

    @field:NotBlank
    val content: String,

    // 작성자는 간단히 nickname 으로 식별(실서비스에선 인증 연동)
    @field:NotBlank
    val nickname: String

)
