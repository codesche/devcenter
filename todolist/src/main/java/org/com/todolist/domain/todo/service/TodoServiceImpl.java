package org.com.todolist.domain.todo.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.todolist.domain.todo.dto.TodoCreateRequest;
import org.com.todolist.domain.todo.dto.TodoResponse;
import org.com.todolist.domain.todo.dto.TodoUpdateRequest;
import org.com.todolist.domain.todo.entity.Todo;
import org.com.todolist.domain.todo.repository.TodoRepository;
import org.com.todolist.global.exception.TodoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    public TodoResponse create(TodoCreateRequest request) {
        Todo saved = todoRepository.save(request.toEntity());
        return TodoResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse get(UUID id) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
        return TodoResponse.from(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TodoResponse> list(Pageable pageable) {
        return todoRepository.findAll(pageable)
            .map(TodoResponse::from);
    }

    @Override
    public TodoResponse update(UUID id, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));
        request.applyTo(todo);      // 부분 업데이트

        //JPA dirty checking 으로 반영
        return TodoResponse.from(todo);
    }

    @Override
    public void delete(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }
}
