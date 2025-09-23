package org.com.ssrboard.service

import jakarta.persistence.EntityNotFoundException
import org.com.ssrboard.domain.Comment
import org.com.ssrboard.domain.Member
import org.com.ssrboard.repository.CommentRepository
import org.com.ssrboard.repository.MemberRepository
import org.com.ssrboard.repository.PostRepository
import org.com.ssrboard.web.dto.CommentCreateRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository,
) {
    fun add(postId: Long, req: CommentCreateRequest) {
        val post = postRepository.findById(postId).orElseThrow()

        val author = memberRepository.findByNickname(req.nickname)
            ?: memberRepository.save(Member.create(req.nickname))

        val comment = Comment.create(req.content, author)
        post.addComment(comment)                            // ← 연관관계 편의 메서드로 post 설정
        commentRepository.save(comment)            // 또는 postRepository.save(post) (cascade ALL 이면)
    }
}