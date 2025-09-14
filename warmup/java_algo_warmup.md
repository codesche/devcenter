
# Java 알고리즘 구현 훈련 정리

## 📌 문제 1: 문자열 뒤집기
### A) StringBuilder 사용
```java
public static String reverseWithBuilder(String s) {
    return new StringBuilder(s).reverse().toString();
}
```

### B) char[] 투 포인터 방식
```java
public static String reverseWithCharArray(String s) {
    char[] arr = s.toCharArray();
    int left = 0;
    int right = arr.length - 1;
    while (left < right) {
        char tmp = arr[left];
        arr[left] = arr[right];
        arr[right] = tmp;
        left++;
        right--;
    }
    return new String(arr);
}
```

---

## 📌 문제 2: 단어 빈도 수 세기
### A) HashMap 기반 반복문
```java
public static Map<String, Integer> wordFreqImperative(String text) {
    Map<String, Integer> freq = new HashMap<String, Integer>();
    String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
    String[] tokens = normalized.trim().split("\\s+");
    for (String t : tokens) {
        if (t.isEmpty()) continue;
        Integer c = freq.get(t);
        if (c == null) c = 0;
        freq.put(t, c + 1);
    }
    return freq;
}
```

### B) Stream API
```java
public static Map<String, Long> wordFreqStream(String text) {
    String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
    return Arrays.stream(normalized.trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.groupingBy(token -> token, Collectors.counting()));
}
```

---

## 📌 문제 3: 정수 리스트 중복 제거 후 정렬
### A) Set 활용
```java
public static List<Integer> dedupeAndSortWithSet(List<Integer> nums) {
    Set<Integer> set = new HashSet<Integer>(nums);
    List<Integer> out = new ArrayList<Integer>(set);
    Collections.sort(out);
    return out;
}
```

### B) Stream API
```java
public static List<Integer> dedupeAndSortStream(List<Integer> nums) {
    return nums.stream().distinct().sorted().collect(Collectors.toList());
}
```

---

## 📌 문제 4: "GROUP BY" 감각 훈련
### A) userId별 주문 수 세기
```java
public static Map<Long, Integer> countOrdersByUser(List<Order> orders) {
    Map<Long, Integer> map = new HashMap<Long, Integer>();
    for (Order o : orders) {
        Integer c = map.get(o.userId);
        if (c == null) c = 0;
        map.put(o.userId, c + 1);
    }
    return map;
}
```

### B) userId별 매출 합계
```java
public static Map<Long, Long> sumAmountByUser(List<Order> orders) {
    return orders.stream().collect(Collectors.groupingBy(
            o -> o.userId,
            Collectors.summingLong(o -> o.amount)
    ));
}
```

### C) 특정 사용자의 최신 주문 찾기
```java
public static Optional<Order> findLatestOrderByUser(List<Order> orders, long userId) {
    return orders.stream()
            .filter(o -> o.userId == userId)
            .max(Comparator.comparing(o -> o.createdAt));
}
```

---

## ✅ 체크포인트 (면접 대응 포인트)
- **문자열 뒤집기**: StringBuilder vs 투 포인터 공간/시간 복잡도 설명 가능 여부
- **단어 빈도 수 세기**: 정규식 전처리, groupingBy + counting Collector 설명 가능 여부
- **중복 제거 정렬**: HashSet → List 변환 후 sort vs distinct().sorted() 차이
- **GROUP BY 감각**: groupingBy, summingLong, max(Comparator) 활용 감각
