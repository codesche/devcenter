package org.com.ssrboard.repository

import org.com.ssrboard.domain.Post
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = ["author"])       // 목록에서 작성자 로딩
    fun findAllByOrderByIdDesc(pageable: Pageable): Page<Post>

    @Query(
        """
            select p from Post p
            left join fetch p.author
            left join fetch p._comments c
            left join fetch c.author
            where p.id = :id
        """
    )

    fun findDetailById(id: Long): Post?

}