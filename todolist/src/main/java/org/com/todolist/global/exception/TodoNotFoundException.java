package org.com.todolist.global.exception;

import java.util.UUID;

// 대상 Todo가 없을 때 사용
public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(UUID id) {
        super("Todo not found: " + id);
    }
}
