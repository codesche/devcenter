package org.com.todolist.domain.todo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.com.todolist.domain.todo.entity.Todo;
import org.com.todolist.global.jpa.JpaAuditingConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@DataJpaTest
@Import(JpaAuditingConfig.class)            // createdAt/updatedAt 자동 주입
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @DisplayName("[JPA] 저장 후 조회 OK (Auditing 필드 포함)")
    void save_and_find() {
        Todo todo = Todo.of("title", "desc", false);
        Todo saved = todoRepository.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Todo found = todoRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("[JPA] 페이지 조회 OK")
    void page_ok() {
        todoRepository.save(Todo.of("t1", "d1", false));
        todoRepository.save(Todo.of("t2","d2",true));
        todoRepository.save(Todo.of("t3","d3",false));

        Pageable pageable = PageRequest.of(0, 2, Direction.DESC, "createdAt");
        Page<Todo> page = todoRepository.findAll(pageable);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("[JPA] 더티 체킹으로 상태 변경 반영 OK")
    void dirty_checking_update() {
        Todo saved = todoRepository.save(Todo.of("before", "d", false));
        saved.markCompleted(true);          // 엔티티 변경

        // @DataJpaTest는 기본적으로 트랜잭션 -> flush는 테스트 종료 시점에
        Todo found = todoRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.isCompleted()).isTrue();
    }

}