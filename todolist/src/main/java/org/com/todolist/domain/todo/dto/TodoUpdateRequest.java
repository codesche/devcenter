package org.com.todolist.domain.todo.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.com.todolist.domain.todo.entity.Todo;

/**
 * Todo 수정 요청 데이터.
 * - 부분 수정(Patch)도 고려하여 필드를 선택적으로 받음.
 */
@Getter
@Setter
public class TodoUpdateRequest {

    @Size(max = 150, message = "제목은 150자를 초과할 수 없습니다.")
    private String title;

    @Size(max = 100, message = "설명은 1000자를 초과할 수 없습니다.")
    private String description;

    private Boolean completed;

    public TodoUpdateRequest() {}

    public TodoUpdateRequest(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    // 기존 엔티티에 변경 사항을 적용 (부분 업데이트 지원)
    public void applyTo(Todo todo) {
        if (title != null) {
            todo.updateTitle(title);
        }

        if (description != null) {
            todo.updateDescription(description);
        }

        if (completed != null) {
            todo.markCompleted(completed);
        }
    }

}
