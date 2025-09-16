package org.com.todolist.domain.todo.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.com.todolist.domain.todo.dto.TodoCreateRequest;
import org.com.todolist.domain.todo.dto.TodoResponse;
import org.com.todolist.domain.todo.dto.TodoUpdateRequest;
import org.com.todolist.domain.todo.service.TodoService;
import org.com.todolist.global.exception.GlobalExceptionHandler;
import org.com.todolist.global.exception.TodoNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TodoController.class)
@Import({GlobalExceptionHandler.class, TodoControllerTest.TestConfig.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /** 테스트 전용 In-Memory Service: 컨트롤러 레이어만 검증 */
    static class InMemoryTodoService implements TodoService {
        private final Map<UUID, TodoResponse> store = new ConcurrentHashMap<>();

        @Override
        public TodoResponse create(TodoCreateRequest request) {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            TodoResponse res = new TodoResponse();

            res = new TodoResponseProxy(id, request.getTitle(), request.getDescription(),
                request.isCompleted(), now, now);

            store.put(id, res);
            return res;
        }

        @Override
        public TodoResponse get(UUID id) {
            TodoResponse res = store.get(id);
            if (res == null) {
                throw new TodoNotFoundException(id);
            }
            return res;
        }

        @Override
        public Page<TodoResponse> list(Pageable pageable) {
            List<TodoResponse> list = new ArrayList<>(store.values());
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), list.size());

            List<TodoResponse> content = start > end ? List.of() : list.subList(start, end);

            return new PageImpl<>(content, pageable, list.size());
        }

        @Override
        public TodoResponse update(UUID id, TodoUpdateRequest request) {
            TodoResponse cur = store.get(id);
            if (cur == null) {
                throw new TodoNotFoundException(id);
            }

            TodoResponseProxy proxy = TodoResponseProxy.from(cur);

            if (request.getTitle() != null) {
                proxy.title = request.getTitle();
            }

            if (request.getDescription() != null) {
                proxy.description = request.getDescription();
            }

            if (request.getCompleted() != null) {
                proxy.completed = request.getCompleted();
            }

            proxy.updatedAt = LocalDateTime.now();
            store.put(id, proxy);

            return proxy;
        }

        @Override
        public void delete(UUID id) {
            if (store.remove(id) == null) {
                throw new TodoNotFoundException(id);
            }
        }

        /** 테스트용 응답 구조체 (필드 접근 위해 내부 클래스 사용) **/
        static class TodoResponseProxy extends TodoResponse {
            private UUID id;
            private String title;
            private String description;
            private boolean completed;
            private LocalDateTime createdAt;
            private LocalDateTime updatedAt;

            public TodoResponseProxy(UUID id, String title, String description, boolean completed,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
                this.id = id;
                this.title = title;
                this.description = description;
                this.completed = completed;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
            }

            static TodoResponseProxy from(TodoResponse r) {
                return new TodoResponseProxy(r.getId(), r.getTitle(), r.getDescription(),
                    r.isCompleted(), r.getCreatedAt(), r.getUpdatedAt());
            }

            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public boolean isCompleted() {
                return completed;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return createdAt;
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return updatedAt;
            }
        }
    }

    static class TestConfig {
        @Bean
        public TodoService todoService() {
            return new InMemoryTodoService();
        }
    }

    @Test
    @DisplayName("[POST /api/todos] 유효성 검증 실패 시 400")
    void create_validation_fail() throws Exception {
        // given
        String body = """
        {   
            "title":"",
            "description":"too simple",
            "completed":false
        }
        """;
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    @DisplayName("[POST /api/todos] 생성 성공 200")
    void create_success() throws Exception {
        String body = """
        {"title":"write tests","description":"controller test","completed":false}
        """;

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.title").value("write tests"))
            .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    @DisplayName("[GET /api/todos/{id}] 없는 리소스 404")
    void get_not_found() throws Exception {
        String randomId = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/todos/{id}", randomId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("[PATCH /api/todos/{id}] 부분 수정 성공 200")
    void patch_success() throws Exception {
        // 먼저 하나 생성
        String create = """
        {"title":"before","description":"d","completed":false}
        """;

        String patch = """
        {"title":"after","completed":true}
        """;

        // 생성하기
        String response = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(create))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // 생성 응답에서 id 추출 (간단히 정규식/문자열 처리)
        String id = response.replaceAll(".*\"id\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        // 패치
        mockMvc.perform(patch("/api/todos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patch))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("after"))
            .andExpect(jsonPath("$.data.completed").value(true));
    }

    @Test
    @DisplayName("[GET /api/todos] 페이지 조회 OK")
    void list_ok() throws Exception {
        // 2개 생성
        mockMvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON)
            .content("""
                      {"title":"t1","description":"d1","completed":false}
                    """));
        mockMvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON)
            .content("""
                      {"title":"t2","description":"d2","completed":true}
                    """));

        mockMvc.perform(get("/api/todos?page=0&size=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content", hasSize(1)))
            .andExpect(jsonPath("$.data.totalElements").value(2));
    }

}