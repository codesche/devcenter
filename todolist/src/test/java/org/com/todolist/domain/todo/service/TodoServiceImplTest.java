package org.com.todolist.domain.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.com.todolist.domain.todo.dto.TodoCreateRequest;
import org.com.todolist.domain.todo.dto.TodoResponse;
import org.com.todolist.domain.todo.dto.TodoUpdateRequest;
import org.com.todolist.domain.todo.entity.Todo;
import org.com.todolist.domain.todo.repository.TodoRepository;
import org.com.todolist.global.exception.TodoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    @Test
    @DisplayName("[create] 요청 DTO -> Entity 저장 -> 응답 DTO 변환 OK")
    void create_success() {
        // given
        TodoCreateRequest request = new TodoCreateRequest("title", "desc", false);
        Todo saved = Todo.of("title", "desc", false);

        // id/시간 필드는 영속성 컨텍스트에서 채워지나, 단위 테스트 같은 경우 null/기본값이어도 무방
        given(todoRepository.save(any(Todo.class))).willReturn(saved);

        // when
        TodoResponse response = todoService.create(request);

        // then
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getDescription()).isEqualTo("desc");

        // 저장 시 넘어간 엔티티 검증하기
        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        verify(todoRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("title");
    }

    @Test
    @DisplayName("[get] 존재하지 않으면 TodoNotFoundException")
    void get_notFound() {
        // given
        UUID id = UUID.randomUUID();
        given(todoRepository.findById(id)).willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> todoService.get(id))
            .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    @DisplayName("[update] 부분 수정 (PATCH) 적용 OK")
    void update_patch_success() {
        // given
        UUID id = UUID.randomUUID();
        Todo entity = Todo.of("before", "desc", false);
        given(todoRepository.findById(id)).willReturn(Optional.of(entity));

        TodoUpdateRequest request = new TodoUpdateRequest("after", null, true);

        // when
        TodoResponse response = todoService.update(id, request);

        // then (더티체킹 전제: 단위테스트에선 단순 필드 변경 확인)
        assertThat(response.getTitle()).isEqualTo("after");
        assertThat(response.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("[list] 페이지 조회 OK")
    void list_success() {
        // given
        Pageable pageable = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        List<Todo> entities = List.of(
            Todo.of("a", "d1", false),
            Todo.of("b", "d2", true)
        );

        given(todoRepository.findAll(pageable))
            .willReturn(new PageImpl<>(entities, pageable, 2));

        // when
        Page<TodoResponse> page = todoService.list(pageable);

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("a");
    }

    @Test
    @DisplayName("[delete] 존재하지 않으면 예외")
    void delete_notFound() {
        // given
        UUID id = UUID.randomUUID();
        given(todoRepository.existsById(id)).willReturn(false);

        // expect
        assertThatThrownBy(() -> todoService.delete(id))
            .isInstanceOf(TodoNotFoundException.class);
    }











}