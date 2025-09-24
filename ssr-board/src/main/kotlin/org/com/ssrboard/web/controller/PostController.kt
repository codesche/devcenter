package org.com.ssrboard.web.controller

import jakarta.validation.Valid
import org.com.ssrboard.service.CommentService
import org.com.ssrboard.service.PostService
import org.com.ssrboard.web.dto.CommentCreateRequest
import org.com.ssrboard.web.dto.PostCreateRequest
import org.com.ssrboard.web.dto.PostUpdateRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/posts")
@Validated
class PostController (
    private val postService: PostService,
    private val commentService: CommentService
) {
    @GetMapping
    fun list(@RequestParam(defaultValue = "0") page: Int,
                    @RequestParam(defaultValue = "10") size: Int,
             model: Model): String {
        val pageResponse = postService.list(page, size)
        model.addAttribute("page", pageResponse)
        return "posts/list"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        val post = postService.getDetail(id)
        model.addAttribute("post", post)
        return "posts/detail"
    }

    @GetMapping("/new")
    fun form(model: Model): String {
        model.addAttribute("postForm",
            PostCreateRequest("", "", "guest"))
        return "posts/form"
    }

    @PostMapping
    fun create(@Valid @ModelAttribute("postForm") req: PostCreateRequest): String {
        val id = postService.create(req)
        return "redirect:/posts/$id"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Long, model: Model): String {
        val post = postService.getDetail(id)
        model.addAttribute("post", post)
        model.addAttribute("updateForm",
            PostUpdateRequest(post.title, post.content))
        return "posts/form"
    }

    @PostMapping("/{id}")
    fun update(@PathVariable id: Long,
               @Valid @ModelAttribute("postForm") req: PostUpdateRequest): String {
        postService.update(id, req)
        return "redirect:/posts/$id"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Long): String {
        postService.delete(id)
        return "redirect:/posts"
    }

    @PostMapping("/{id}/comments")
    fun addComment(@PathVariable id: Long,
                   @Valid @ModelAttribute("commentForm") req: CommentCreateRequest): String {
        commentService.add(id, req)
        return "redirect:/posts/$id"
    }


}