# Java 알고리즘 구현 체크포인트 심화 정리

면접 대비를 위한 각 체크포인트별 설명 및 추가 심화 포인트 정리입니다.

---

## 1) 문자열 뒤집기 — StringBuilder vs 투 포인터

**공통 복잡도**
- 시간: 둘 다 O(n)  
- 공간:
  - `StringBuilder.reverse()` → 새 내부 버퍼가 필요해 **O(n)**
  - `char[]` 투 포인터 → 배열을 이미 갖고 있으면 **O(1)** 추가 공간 (단, `String` → `char[]` 변환은 O(n))

**선택 기준**
- 빠르게 한 줄로 끝내고 싶다 → **StringBuilder**
- 배열/버퍼를 직접 다루는 상황 → **투 포인터**
- **스레드 안전** 필요 → `StringBuffer` (대부분 불필요)

**심화 포인트**
- Java `String`은 **immutable** → 조작 시 새 객체 생성
- **서러게이트 페어(이모지 등)** 문제: `char[]` 뒤집기 시 깨짐 가능 → `codePoint` 단위 처리 필요
- 마이크로벤치마크 시 JIT·GC 영향 고려

**예시 (서러게이트 안전)**
```java
public static String reverseByCodePoint(String input) {
    int[] codePoints = input.codePoints().toArray();
    for (int left = 0, right = codePoints.length - 1; left < right; left++, right--) {
        int temporary = codePoints[left];
        codePoints[left] = codePoints[right];
        codePoints[right] = temporary;
    }
    return new String(codePoints, 0, codePoints.length);
}
```

---

## 2) 단어 빈도 수 세기 — 정규식 전처리 & groupingBy + counting

**핵심 흐름**
1) 정규화(소문자, 불필요 문자 제거)  
2) 토큰화(공백 등)  
3) 집계(Map 또는 `Collectors.groupingBy`)

**심화 포인트**
- 정규식 `[^a-z0-9]` 대신 다국어는 `\p{L}`, `\p{N}`
- 로케일 안전: `toLowerCase(Locale.ROOT)`
- 자주 쓰는 정규식은 `Pattern`으로 캐싱
- 순서 보존 필요 → `LinkedHashMap`
- **빈도 순 정렬** 자주 면접 출제

**예시**
```java
public static LinkedHashMap<String, Long> topFrequencies(String text, Set<String> stopwords, int limit) {
    return Arrays.stream(text.toLowerCase(Locale.ROOT).split("\\P{L}+"))
        .filter(token -> !token.isEmpty())
        .filter(token -> !stopwords.contains(token))
        .collect(Collectors.groupingBy(token -> token, Collectors.counting()))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                .thenComparing(Map.Entry.comparingByKey()))
        .limit(limit)
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (a, b) -> a,
            LinkedHashMap::new
        ));
}
```

---

## 3) 중복 제거 후 정렬 — HashSet→sort vs distinct().sorted()

**비교**
- HashSet → List → Collections.sort()  
  - 시간: O(n) + O(k log k)
  - 장점: 명령형 스타일
- stream.distinct().sorted()  
  - 선언적 스타일
- TreeSet 활용 → **중복 제거 + 정렬 한 번에**

**심화 포인트**
- `equals/hashCode` vs `Comparable/Comparator` 차이 설명
- 순서 유지 필요 → LinkedHashSet
- 대규모 primitive → IntStream 사용
- 커스텀 객체: equals와 compareTo 불일치 이슈

**예시**
```java
public static List<Integer> deduplicateAndSort(List<Integer> numbers) {
    return new ArrayList<>(new TreeSet<>(numbers));
}
```

---

## 4) GROUP BY 감각 — groupingBy, summingLong, max(Comparator)

**SQL ↔ Java 매핑**
- GROUP BY user_id → groupingBy(Order::getUserId, ...)
- SUM(amount) → summingLong(Order::getAmount)
- MAX(created_at) → maxBy(Comparator.comparing(Order::getCreatedAt))

**패턴**
- 합계/평균/개수: summingLong, averagingInt, counting
- 전처리 후 집계: mapping, filtering, collectingAndThen
- 결과 Map 타입 제어: groupingBy(key, LinkedHashMap::new, downstream)

**심화 포인트**
- 최신 주문 1건: maxBy 또는 toMap 병합 함수
- 대규모 → groupingByConcurrent + parallelStream
- SQL과 달리 없는 그룹은 안 생김 → Optional 처리 필요

**예시**
```java
public static Map<Long, Long> totalAmountByUser(List<Order> orders) {
    return orders.stream().collect(Collectors.groupingBy(
        Order::getUserId,
        Collectors.summingLong(Order::getAmount)
    ));
}

public static Map<Long, Order> latestOrderByUser(List<Order> orders) {
    return orders.stream().collect(Collectors.toMap(
        Order::getUserId,
        order -> order,
        (existing, candidate) -> candidate.getCreatedAt().isAfter(existing.getCreatedAt()) ? candidate : existing
    ));
}
```

---

## 추가 면접 팁

- 복잡도 표현 시 입력 크기 n, 유니크 k 구분 → O(n + k log k)  
- 데이터 품질 고려: 유니코드, 불용어, 토큰화 규칙 등  
- 불변/가변 차이: String vs StringBuilder → GC·할당 비용까지 설명 가능  
- 테스트 전략: 빈 입력, 특수문자, 대용량, null 처리  
- API 선택 이유 근거 제시: 상황 따라 HashSet vs TreeSet 등
