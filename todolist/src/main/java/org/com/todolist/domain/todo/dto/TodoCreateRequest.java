package org.com.todolist.domain.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.com.todolist.domain.todo.entity.Todo;

@Getter
@Setter
public class TodoCreateRequest {

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 150, message = "제목은 150자를 초과할 수 없습니다.")
    private String title;

    @Size(max = 1000, message = "설명은 1000자를 초과할 수 없습니다.")
    private String description;

    // 생성 시 완료 여부를 함께 받을지의 여부는 정책에 따라 결정
    private boolean completed;

    public TodoCreateRequest() {}

    public TodoCreateRequest(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    // DTO -> Entity 변환(선호 기준: DTO 내부에 변환 로직)
    public Todo toEntity() {
        return Todo.of(title, description, completed);
    }

}
