
import java.util.*;

// 회의실 최소 개수

/**
 * 총 시간 복잡도: O(n log n)
 * 총 공간 복잡도: O(n)
 * == 동작 ==
 * 1. 시작 시간이 빠른 회의부터 순서대로 본다.
 * 2. 현재 가장 빨리 끝나는 회의(힙 top)의 종료시간이 시작시간 <= start라면 그 방을 재사용할 수 있다. => poll()
 * 3. 현재 회의의 종료 시간을 힙에 넣는다(offer(end)).
 * 4. 어떤 시점에서든 힙에 들어있는 원소 수 = 그 시점에 동시에 진행 중인 회의 수.
 * 5. 모든 회의를 처리한 뒤 힙 크기 = 필요한 최소 회의실 수.
 */
public class HeapEx {

    public int minRooms(int[][] intervals) {
        // 예외 처리
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // 회의 시작 시간 기준 정렬 -> O(n log n)
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // 최소 힙(PriorityQueue)으로 "회의 종료 시간" 관리 - 종료시간 최소힙
        PriorityQueue<Integer> pq = new PriorityQueue<>();

        // 모든 회의 순회
        for (int[] it : intervals) {
            int start = it[0];
            int end = it[1];

            if (!pq.isEmpty() && pq.peek() <= start) {      // O(log n)
                pq.poll();
            }

            pq.offer(end);
        }

        return pq.size();
    }

}
