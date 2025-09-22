package org.com.ssrboard.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class PostUpdateRequest (

    @field:NotBlank
    @field:Size(max = 200)
    val title: String,

    @field:NotBlank
    val content: String

)