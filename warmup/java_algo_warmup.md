# Java 알고리즘 구현 훈련 정리 (리팩토링 버전)

한 글자 변수명을 지양하고, 의미 있는 변수명을 사용하도록 리팩토링한 버전입니다.

<br/>

## 📌 문제 1: 문자열 뒤집기

### ReverseStringEx1.java (StringBuilder 사용)
```java
package reversestring;

// 문자열 뒤집기
public class ReverseStringEx1 {

    // StringBuilder 활용
    public static String reverseWithBuilder(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    public static void main(String[] args) {
        String str = "HelloWorld";
        System.out.println("reverseWithBuilder: " + reverseWithBuilder(str));   // 기대: dlroWolleH
    }

}

```

<br/>

### ReverseStringEx2.java (char[] 투 포인터)
```java
package reversestring;

// 문자열 뒤집기 - char[] 양끝 포인터 스왑
public class ReverseStringEx2 {

    public static String reverseWithCharArray(String str) {
        // TODO: 투 포인터로 char[] 를 뒤집기
        char[] arr = str.toCharArray();
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

    public static void main(String[] args) {
        String str = "HelloWorld";
        System.out.println("reverseWithCharArray: " + reverseWithCharArray(str));   // 기대: dlroWolleH
    }

}

```

<br/>

## 📌 문제 2: 단어 빈도 수 세기

### WordFreqEx1.java
```java
package countingwords;

import java.util.*;
import java.util.stream.*;

// 단어 빈도 수 세기
// 구두점 제거, 소문자 변환, 공백 분리 기준으로 단어 수 세기
public class WordFreqEx1 {

    // 구두점 제거, 소문자 변환, 공백 분리
    public static Map<String, Integer> wordFreq(String text) {
        Map<String, Integer> freq = new HashMap<>();

        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] tokens = normalized.trim().split("\\s+");
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }

            Integer cnt = freq.get(token);

            if (cnt == null) {
                cnt = 0;
            }

            freq.put(token, cnt + 1);
        }

        return freq;
    }

    // Stream API
    public static Map<String, Long> wordFreqStream(String text) {
        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        return Arrays.stream(normalized.trim().split("\\s+"))
            .filter(token -> !token.isEmpty())
            .collect(Collectors.groupingBy(token -> token, Collectors.counting()));
    }

    public static void main(String[] args) {
        String text = "Hello, hello!! Java & SQL—sql. JAVA?";
        Map<String, Integer> freq1 = wordFreq(text);
        Map<String, Long> freq2 = wordFreqStream(text);

        // 기대(대략): {hello=2, java=2, sql=2}
        System.out.println("[P2] wordFreq: " + freq1);
        System.out.println("[P2] wordFreqStream: " + freq2);
    }

}

```

<br/>

## 📌 문제 3: 정수 리스트 중복 제거 후 정렬

### SortEx1.java
```java
package sort;

import java.util.*;
import java.util.stream.*;

// 정수 리스트 중복 제거 후 정렬
public class SortEx1 {

    // Set 활용
    public static List<Integer> dedupeAndSortWithSet(List<Integer> nums) {
        // TODO: HashSet으로 중복 제거 후 ArrayList로 옮겨 sort
        Set<Integer> set = new HashSet<>(nums);
        List<Integer> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    // Stream distinct + sorted
    public static List<Integer> dedupeAndSortStream(List<Integer> nums) {
        return nums.stream().distinct().sorted().collect(Collectors.toList());
    }

    public static void main(String[] args) {
        // 테스트
        List<Integer> nums = Arrays.asList(5, 3, 3, 9, 1, 5, 7, 1, 2);
        System.out.println("[P3] dedupeAndSortWithSet: " + dedupeAndSortWithSet(nums));   // 기대: [1,2,3,5,7,9]
        System.out.println("[P3] dedupeAndSortStream: " + dedupeAndSortStream(nums));     // 기대: [1,2,3,5,7,9]
    }

}

```

<br/>

## 📌 문제 4: GROUP BY 감각 훈련

### Order.java
```java
package groupby;

import java.util.*;
import java.util.stream.*;
import java.time.*;

// 가상의 주문 도메인: userId별 주문 수 & 합계, 최근 주문 찾기
public class Order {

    public final long id;
    public final long userId;
    public final long amount;                   // 금액 (원)
    public final LocalDateTime createdAt;

    public Order(long id, long userId, long amount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId +
            ", amount=" + amount + ", createdAt=" + createdAt + '}';
    }

    // userId별 주문 수
    public static Map<Long, Integer> countOrdersByUser(List<Order> orders) {
        Map<Long, Integer> map = new HashMap<>();
        for (Order o : orders) {
            Integer cnt = map.get(o.userId);
            if (cnt == null) {
                cnt = 0;
            }
            map.put(o.userId, ++cnt);
        }

        return map;
    }

    // userId별 매출 합계 (stream)
    public static Map<Long, Long> sumAmountByUser(List<Order> orders) {
        return orders.stream().collect(Collectors.groupingBy(
            o -> o.userId,
            Collectors.summingLong(o -> o.amount)
        ));
    }

    // 특정 사용자 최신 주문 (createdAt DESC LIMIT 1)
    public static Optional<Order> findLatestOrderByUser(List<Order> orders, long userId) {
        return orders.stream()
            .filter(o -> o.userId == userId)
            .max(Comparator.comparing(o -> o.createdAt));
    }

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
            new Order(1, 101, 12000, LocalDateTime.of(2025, 9, 10, 14, 0)),
            new Order(2, 101, 8000,  LocalDateTime.of(2025, 9, 12, 9, 30)),
            new Order(3, 102, 5000,  LocalDateTime.of(2025, 9, 12, 10, 0)),
            new Order(4, 103, 3000,  LocalDateTime.of(2025, 9, 13, 20, 15)),
            new Order(5, 101, 16000, LocalDateTime.of(2025, 9, 14, 8, 45)),
            new Order(6, 102, 7000,  LocalDateTime.of(2025, 9, 14, 9, 0))
        );

        Map<Long, Integer> cntByUser = countOrdersByUser(orders);
        Map<Long, Long> sumByUser = sumAmountByUser(orders);
        Optional<Order> latest101 = findLatestOrderByUser(orders, 101L);

        System.out.println("[P4] countOrdersByUser: " + cntByUser); // 기대: {101=3, 102=2, 103=1}
        System.out.println("[P4] sumAmountByUser: " + sumByUser);   // 기대: {101=36000, 102=12000, 103=3000}
        System.out.println("[P4] latest order of 101: " + (latest101.isPresent() ? latest101.get() : "N/A"));
        // 기대: id=5 (2025-09-14 08:45)
    }

}

```

<br/>

## ✅ 체크포인트 (면접 대응 포인트)
- **문자열 뒤집기**: StringBuilder vs 투 포인터 공간/시간 복잡도 설명 가능 여부
- **단어 빈도 수 세기**: 정규식 전처리, groupingBy + counting Collector 설명 가능 여부
- **중복 제거 정렬**: HashSet → List 변환 후 sort vs distinct().sorted() 차이
- **GROUP BY 감각**: groupingBy, summingLong, max(Comparator) 활용 감각
