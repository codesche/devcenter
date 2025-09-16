package org.com.todolist.domain.todo.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.com.todolist.domain.todo.dto.TodoCreateRequest;
import org.com.todolist.domain.todo.dto.TodoResponse;
import org.com.todolist.domain.todo.dto.TodoUpdateRequest;
import org.com.todolist.domain.todo.service.TodoService;
import org.com.todolist.global.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> create(@Valid @RequestBody TodoCreateRequest request) {
        TodoResponse result = todoService.create(request);
        return ResponseEntity.ok(ApiResponse.ok(result, "created"));
    }

    // READ - 단건
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(todoService.get(id),"get"));
    }

    // READ - 페이지
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TodoResponse>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(todoService.list(pageable)));
    }

    // UPDATE (부분 수정 가능)
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponse>> update(@PathVariable UUID id,
        @Valid @RequestBody TodoUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(todoService.update(id, request), "updated"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        todoService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "deleted"));
    }

}




















