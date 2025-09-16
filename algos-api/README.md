# 📘 Project 1 — Spring Boot 알고리즘 API (with Code Comments)

## 📌 개요
- 배열/문자열 입력을 받아 알고리즘 풀이 결과를 반환하는 **REST API**
- **구성**: Spring Boot 3계층 구조 (Controller → Service → DTO/Model)
- **전역 예외 처리**, **공통 응답 객체**, **단위/슬라이스 테스트**
- **DB 미사용**, 순수 연산 API

---

## 🏗️ 프로젝트 구조
```
algos-api
├─ common
│  ├─ api/ApiResponse.java
│  ├─ exception/{DomainException, ErrorCode, GlobalExceptionHandler}.java
├─ algorithm
│  ├─ controller/AlgorithmController.java
│  ├─ dto/request/{...}Request.java
│  ├─ dto/response/{...}Response.java
│  ├─ model/WordCount.java
│  ├─ service/AlgorithmService.java
│  └─ service/impl/AlgorithmServiceImpl.java
└─ test
   ├─ AlgorithmServiceImplTest.java
   ├─ AlgorithmControllerTest.java
   └─ GlobalExceptionHandlerTest.java
```

---

## 🧱 공통 모듈 코드 (주석 포함)

### `ApiResponse<T>`
```java
// 모든 API 응답을 공통 형태로 감싸는 클래스
// success: 성공 여부, message: 응답 메시지, data: 실제 데이터, timestamp: 응답 시각
public class ApiResponse<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final Instant timestamp;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now(); // 응답 시각 기록
    }
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, "OK", data); }
    public static <T> ApiResponse<T> fail(String message) { return new ApiResponse<>(false, message, null); }

    // Getter 메서드들
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Instant getTimestamp() { return timestamp; }
}
```

### `DomainException` & `ErrorCode`
```java
// 도메인 레벨의 의미 있는 예외 정의
public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;
    public DomainException(ErrorCode errorCode, String detail){
        super(detail);
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode(){ return errorCode; }
}

// 에러 코드 Enum
public enum ErrorCode {
    INVALID_ARGUMENT(400, "Invalid argument"),
    NOT_FOUND(404, "Not found"),
    INTERNAL_ERROR(500, "Internal server error");
    // ...
}
```

### `GlobalExceptionHandler`
```java
// 모든 Controller 계층에서 발생하는 예외를 한 곳에서 처리
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO Validation 실패 시
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ApiResponse<?>> handleValidation(Exception ex){
        return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed: " + ex.getMessage()));
    }

    // 도메인 예외 처리
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<?>> handleDomain(DomainException ex){
        var code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.fail(code.getMessage()+": "+ex.getMessage()));
    }

    // 그 외 예상치 못한 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOthers(Exception ex){
        return ResponseEntity.internalServerError().body(ApiResponse.fail("Unexpected error"));
    }
}
```

---

## 👩‍💻 알고리즘 서비스 코드 (주석 포함)

### `AlgorithmServiceImpl`
```java
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final Pattern WORD = Pattern.compile("[a-zA-Z0-9']+");

    // 문자열 뒤집기 (O(n))
    @Override
    public ReverseStringResponse reverse(ReverseStringRequest request) {
        var input = request.getText();
        var sb = new StringBuilder(input);
        return new ReverseStringResponse(sb.reverse().toString());
    }

    // 단어 빈도 수 계산 (해시맵 누적 + 정렬)
    @Override
    public WordFrequencyResponse wordFrequency(WordFrequencyRequest request) {
        String lower = request.getText().toLowerCase(Locale.ROOT);
        Matcher m = WORD.matcher(lower);
        Map<String, Long> map = new HashMap<>();
        while (m.find()) {
            map.merge(m.group(), 1L, Long::sum);
        }
        // 빈도 내림차순, 단어 알파벳순 정렬
        List<WordCount> list = map.entrySet().stream()
                .map(e -> new WordCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(WordCount::getCount).reversed()
                        .thenComparing(WordCount::getWord))
                .collect(Collectors.toList());
        return new WordFrequencyResponse(list);
    }

    // Two Sum 문제 (HashMap 이용, O(n))
    @Override
    public TwoSumResponse twoSum(TwoSumRequest request) {
        int[] nums = request.getNumbers();
        int target = request.getTarget();
        Map<Integer, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            Integer j = indexByValue.get(need);
            if (j != null) return new TwoSumResponse(j, i);
            indexByValue.put(nums[i], i);
        }
        throw new DomainException(ErrorCode.NOT_FOUND, "No two-sum solution");
    }

    // 중복 제거 + 정렬 (Stream API)
    @Override
    public UniqueSortResponse uniqueSort(UniqueSortRequest request) {
        int[] arr = request.getNumbers();
        return new UniqueSortResponse(
            Arrays.stream(arr).distinct().sorted().toArray()
        );
    }

    // 괄호 유효성 검사 (Stack)
    @Override
    public ValidParenthesesResponse validParentheses(ValidParenthesesRequest request) {
        String s = request.getS();
        Deque<Character> st = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c=='('||c=='['||c=='{') st.push(c);
            else if (c==')'||c==']'||c=='}') {
                if (st.isEmpty()) return new ValidParenthesesResponse(false);
                char top = st.pop();
                if (!matches(top, c)) return new ValidParenthesesResponse(false);
            }
        }
        return new ValidParenthesesResponse(st.isEmpty());
    }

    // 괄호 매칭 여부 확인
    private boolean matches(char open, char close){
        return (open=='(' && close==')') || (open=='[' && close==']') || (open=='{' && close=='}');
    }
}
```

---

## 🎮 Controller (주석 포함)
```java
@RestController
@RequestMapping("/api/v1/algorithms")
public class AlgorithmController {

    private final AlgorithmService service;
    public AlgorithmController(AlgorithmService service){ this.service = service; }

    // 문자열 뒤집기 API
    @PostMapping("/reverse")
    public ApiResponse<ReverseStringResponse> reverse(@Valid @RequestBody ReverseStringRequest req){
        return ApiResponse.ok(service.reverse(req));
    }

    // 단어 빈도 수 API
    @PostMapping("/word-frequency")
    public ApiResponse<WordFrequencyResponse> wordFrequency(@Valid @RequestBody WordFrequencyRequest req){
        return ApiResponse.ok(service.wordFrequency(req));
    }

    // Two Sum API
    @PostMapping("/two-sum")
    public ApiResponse<TwoSumResponse> twoSum(@Valid @RequestBody TwoSumRequest req){
        return ApiResponse.ok(service.twoSum(req));
    }

    // 중복 제거 정렬 API
    @PostMapping("/unique-sort")
    public ApiResponse<UniqueSortResponse> uniqueSort(@Valid @RequestBody UniqueSortRequest req){
        return ApiResponse.ok(service.uniqueSort(req));
    }

    // 괄호 유효성 검사 API
    @PostMapping("/valid-parentheses")
    public ApiResponse<ValidParenthesesResponse> validParentheses(@Valid @RequestBody ValidParenthesesRequest req){
        return ApiResponse.ok(service.validParentheses(req));
    }
}
```

---

## 🧪 테스트 (주석 포함)

### `AlgorithmServiceImplTest`
```java
class AlgorithmServiceImplTest {
    private final AlgorithmServiceImpl service = new AlgorithmServiceImpl();

    @Test
    void reverse_ok(){
        // 문자열 뒤집기 검증
        var res = service.reverse(new ReverseStringRequest("abcd"));
        assertEquals("dcba", res.getReversed());
    }

    @Test
    void word_frequency_ok(){
        // 단어 빈도 수 검증 (hello 2회)
        var res = service.wordFrequency(new WordFrequencyRequest("Hello, hello world!"));
        assertEquals(2, res.getFrequencies().get(0).getCount());
    }

    @Test
    void two_sum_ok(){
        // 2+7=9 → (0,1) 반환
        var res = service.twoSum(new TwoSumRequest(new int[]{2,7,11,15}, 9));
        assertEquals(0, res.getIndex1());
        assertEquals(1, res.getIndex2());
    }
}
```

---

## 🌐 API 요약
- `POST /api/v1/algorithms/reverse`
- `POST /api/v1/algorithms/word-frequency`
- `POST /api/v1/algorithms/two-sum`
- `POST /api/v1/algorithms/unique-sort`
- `POST /api/v1/algorithms/valid-parentheses`

---

## 📝 설계 메모
- **시간 복잡도**
  - Reverse: O(n)
  - Word Frequency: O(n + k log k)
  - Two Sum: O(n)
  - Unique Sort: O(n log n)
  - Valid Parentheses: O(n)
- **확장성**: Redis 캐싱, JWT 인증, 모니터링/로그 추가 가능
