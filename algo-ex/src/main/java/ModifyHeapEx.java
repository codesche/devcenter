
import java.util.*;

public class ModifyHeapEx {

    public int minRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int i = 0;
        int j = 0;
        int rooms = 0;
        int maxRooms = 0;

        while (i < n) {
            // 반열림 구간 기준; starts[i] < ends[j] 새 방 필요
            if (starts[i] < ends[j]) {
                rooms++;
                maxRooms = Math.max(maxRooms, rooms);
                i++;
            } else {        // starts[i] >= ends[j] -> 방 회수 가능
                rooms--;
                j++;
            }
        }

        return maxRooms;
    }

    private void run(String name, int[][] intervals, int expected) {
        int resHeap = minRooms(copyOf(intervals));      // 힙 버전
        int resTwoPtr = minRooms(copyOf(intervals));   // 투 포인터 버전

        System.out.printf("[%s] 힙: %d, 투 포인터: %d, 기대: %d -> %s%n",
            name, resHeap, resTwoPtr, expected,
            (resHeap == expected && resTwoPtr == expected) ? "PASS" : "FAIL");

        // 디버깅용 상세 출력 원하면 아래 주석 해제
        // System.out.println("입력: " + Arrays.deepToString(intervals));
    }

    // 방어적으로 입력을 복사 (정렬/변형으로 원본 영향 방지)
    private int[][] copyOf(int[][] arr) {
        int[][] cpy = new int[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            cpy[i] = Arrays.copyOf(arr[i], arr[i].length);
        }
        return cpy;
    }

    // === 메인: 테스트 실행 ===
    public static void main(String[] args) {
        ModifyHeapEx t = new ModifyHeapEx();

        // 테스트 케이스들
        int[][] case1 = { {0,30}, {5,10}, {15,20} };            // 기대: 2
        int[][] case2 = { {7,10}, {10,12} };                    // 반열림 기준: 1
        int[][] case3 = { {1,4}, {2,5}, {3,6} };               // 전부 겹침: 3
        int[][] case4 = { {1,2}, {2,3}, {3,4} };               // 체인: 1
        int[][] case5 = {};                                     // 빈 입력: 0
        int[][] case6 = { {1,2} };                              // 단일: 1
        int[][] case7 = { {1,10}, {2,3}, {3,4}, {4,5}, {6,9} }; // 내부 많은 회의: 2

        // 반열림([start, end)) 기준 기대값
        t.run("case1", case1, 2);
        t.run("case2", case2, 1);
        t.run("case3", case3, 3);
        t.run("case4", case4, 1);
        t.run("case5", case5, 0);
        t.run("case6", case6, 1);
        t.run("case7", case7, 2);
    }

}
