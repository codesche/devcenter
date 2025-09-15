
# 배열 회전 알고리즘 분석

## 📌 주어진 코드

```java
public int[] solution(int[] A, int K) {
    int N = A.length;
    if (N == 0) {
        return A;
    }

    int[] rotated = new int[N];
    K %= N;                     // 불필요한 회전 줄이기 (K가 배열 크기보다 큰 경우를 고려)

    for (int i = 0; i < N; i++) {
        int newIndex = (i + K) % N;
        rotated[newIndex] = A[i];
    }

    return rotated;
}
```

---

## 📌 시간 복잡도

- `for` 루프: `O(N)`
- 나머지 연산: `O(1)`
- **총 시간 복잡도:**

```
O(N)
```

---

## 📌 공간 복잡도

- 입력 배열 `A`는 그대로 유지
- 결과 배열 `rotated` 추가 생성 → `O(N)`
- **총 공간 복잡도:**

```
O(N)
```

---

## 📌 보완할 수 있는 부분

현재 방식:
- ✅ 직관적이고 이해하기 쉬움
- ❌ 추가 배열 필요 (`O(N)` 공간)

### ➡️ 개선 아이디어: 제자리 회전 (In-Place Rotation)

- 배열 전체 뒤집기
- 앞쪽 K개 다시 뒤집기
- 나머지 뒤집기

```java
public int[] solution(int[] A, int K) {
    int N = A.length;
    if (N == 0) return A;

    K %= N;
    reverse(A, 0, N - 1);
    reverse(A, 0, K - 1);
    reverse(A, K, N - 1);

    return A;
}

private void reverse(int[] A, int start, int end) {
    while (start < end) {
        int temp = A[start];
        A[start] = A[end];
        A[end] = temp;
        start++;
        end--;
    }
}
```

### 개선 후 복잡도
- **시간 복잡도:** `O(N)`
- **공간 복잡도:** `O(1)`

---

## 📌 정리

| 방식          | 시간 복잡도 | 공간 복잡도 |
|---------------|-------------|-------------|
| 기존 코드     | `O(N)`      | `O(N)`      |
| 개선된 코드   | `O(N)`      | `O(1)`      |
