
# 회의실 최소 개수 알고리즘 분석

## 📌 코드

```java
import java.util.*;

public class HeapEx {

    public int minRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        for (int[] it : intervals) {
            int start = it[0];
            int end = it[1];

            if (!pq.isEmpty() && pq.peek() <= start) {
                pq.poll();
            }

            pq.offer(end);
        }

        return pq.size();
    }
}
```

---

## 📌 시간 복잡도

- 정렬: `O(n log n)`
- 힙 연산: 각 회의마다 `O(log n)` → 총 `O(n log n)`
- **총 시간 복잡도: `O(n log n)`**

---

## 📌 공간 복잡도

- 최소 힙(PriorityQueue): 최악의 경우 `n`개 저장 → `O(n)`
- **총 공간 복잡도: `O(n)`**

---

## 📌 동작 원리

1. 회의를 시작시간 기준으로 정렬
2. 힙에 종료시간을 넣고, 가장 빨리 끝나는 회의(`pq.peek()`)가 현재 시작시간보다 작거나 같으면 재사용 가능 → `poll()`
3. 새로운 회의를 힙에 추가
4. 모든 회의를 처리한 뒤 힙 크기 = 필요한 최소 회의실 개수

---

## 📌 코너 케이스

- **구간 경계**
  - `pq.peek() <= start` → `[start, end)` 반열림 구간으로 간주 (끝=시작이면 겹치지 않음)
  - 폐구간 모델(끝과 시작이 같아도 겹침)로 보려면 `< start`로 수정
- **잘못된 구간**: `start > end` 같은 입력은 별도 검증 필요
- **빈 배열/하나의 회의**: 정상적으로 0 또는 1 반환

---

## 📌 대안 풀이 (두 포인터 방식)

```java
public int minRooms2(int[][] intervals) {
    if (intervals == null || intervals.length == 0) return 0;

    int n = intervals.length;
    int[] starts = new int[n];
    int[] ends = new int[n];
    for (int i = 0; i < n; i++) {
        starts[i] = intervals[i][0];
        ends[i] = intervals[i][1];
    }
    Arrays.sort(starts);
    Arrays.sort(ends);

    int i = 0, j = 0, rooms = 0, maxRooms = 0;
    while (i < n) {
        if (starts[i] < ends[j]) { // 반열림 구간
            rooms++;
            maxRooms = Math.max(maxRooms, rooms);
            i++;
        } else {
            rooms--;
            j++;
        }
    }
    return maxRooms;
}
```

- 시간 복잡도: `O(n log n)`
- 공간 복잡도: `O(n)`

---

## 📌 테스트 아이디어

- `[[0,30],[5,10],[15,20]]` → 2  
- `[[7,10],[10,12]]` → 반열림=1, 폐구간=2  
- `[[1,4],[2,5],[3,6]]` → 3  
- `[[1,2],[2,3],[3,4]]` → 반열림=1  
- `[]` → 0, `[[1,2]]` → 1  

---

## 📌 개선 포인트

- 입력 검증(`start > end` 방지)
- 경계조건 정책(반열림/폐구간) 주석으로 명시
- 큰 입력에서는 힙 대신 두 포인터 방식 추천 (상수 인자 작음)
