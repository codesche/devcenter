package org.com.ssrboard.service

import org.com.ssrboard.domain.Member
import org.com.ssrboard.domain.Post
import org.com.ssrboard.global.error.EntityNotFoundException
import org.com.ssrboard.repository.PostRepository
import org.com.ssrboard.util.PageResponse
import org.com.ssrboard.web.dto.PostCreateRequest
import org.com.ssrboard.web.dto.PostResponse
import org.com.ssrboard.web.dto.PostUpdateRequest
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService (
    private val postRepository: PostRepository
) {
    fun list(page: Int, size: Int): PageResponse<PostResponse> {
        val paging = PageRequest.of(
            page.coerceAtLeast(0), size.coerceIn(1, 50)
        )
        val result = postRepository.findAllByOrderByIdDesc(paging)
            .map {
                PostResponse.from(it)
            }

        return PageResponse.of(result)
    }

    fun getDetail(id: Long): Post {
        return postRepository.findDetailById(id)
            ?: throw EntityNotFoundException("Post($id) not found")
    }

    @Transactional
    fun create(req: PostCreateRequest): Long {
        val author = Member.create(req.nickname)
        val post = Post.create(
            title = req.title,
            content = req.content,
            author = author             // 영속 상태
        )
        postRepository.save(post)
        return post.id!!
    }

    @Transactional
    fun update(id: Long, req: PostUpdateRequest) {
        val post = postRepository.findById(id)
             .orElseThrow { EntityNotFoundException("Post($id) not found") }
        post.update(req.title, req.content)     // dirty-checking
    }

    @Transactional
    fun delete(id: Long) {
        val post = postRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Post($id) not found") }

        // Comment orphanRemoval에 의해 함께 삭제
        postRepository.delete(post)
    }

}