package org.com.todolist.domain.todo.repository;

import java.util.UUID;
import org.com.todolist.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

}
