package org.com.todolist.domain.todo.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.com.todolist.domain.todo.entity.Todo;

@Getter
public class TodoResponse {

    private UUID id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TodoResponse() {}

    public TodoResponse(UUID id, String title, String description, boolean completed,
        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Entity -> DTO 변환
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getDescription(),
            todo.isCompleted(),
            todo.getCreatedAt(),
            todo.getUpdatedAt()
        );
    }

}
