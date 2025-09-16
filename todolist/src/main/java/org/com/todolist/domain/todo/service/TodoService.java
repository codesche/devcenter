package org.com.todolist.domain.todo.service;

import java.util.UUID;
import org.com.todolist.domain.todo.dto.TodoCreateRequest;
import org.com.todolist.domain.todo.dto.TodoResponse;
import org.com.todolist.domain.todo.dto.TodoUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// 반환은 항상 DTO
public interface TodoService {

    TodoResponse create(TodoCreateRequest request);

    TodoResponse get(UUID id);

    Page<TodoResponse> list(Pageable pageable);

    TodoResponse update(UUID id, TodoUpdateRequest request);

    void delete(UUID id);

}
