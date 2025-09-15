

/**
 * 개선 후 복잡도
 * - 시간 복잡도: O(N), 세 번의 reverse 연산, 각가 최대 N/2 swap
 * - 공간 복잡도: O(1), 상수 변수만 사용
 *
 * // A = [3, 8, 9, 7, 6], K = 3
 * // => [9, 7, 6, 3, 8]
 */

public class ModifyRotateArrayVer {

    public int[] solution(int[] A, int K) {
        int N = A.length;

        if (N == 0) {
            return A;
        }

        K %= N;

        reverse(A, 0, N - 1);           // 전체 뒤집기
        reverse(A, 0, K - 1);           // 앞쪽 3개 뒤집기
        reverse(A, K, N - 1);                  // 나머지 뒤집기

        return A;           // 새로운 배열 없이 그대로 생성
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

    private void printArray(int[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    public static void main(String[] args) {
        ModifyRotateArrayVer test = new ModifyRotateArrayVer();

        // 테스트 케이스 1
        int[] case1 = {3, 8, 9, 7, 6};
        int K1 = 3;
        System.out.println("테스트 1: " + K1 + "번 회전");
        test.printArray(test.solution(case1, K1)); // 예상: [9, 7, 6, 3, 8]

        // 테스트 케이스 2
        int[] case2 = {0, 0, 0};
        int K2 = 1;
        System.out.println("테스트 2: " + K2 + "번 회전");
        test.printArray(test.solution(case2, K2)); // 예상: [0, 0, 0]

        // 테스트 케이스 3
        int[] case3 = {1, 2, 3, 4};
        int K3 = 4;
        System.out.println("테스트 3: " + K3 + "번 회전");
        test.printArray(test.solution(case3, K3)); // 예상: [1, 2, 3, 4] (회전 수 = 배열 길이)

        // 테스트 케이스 4
        int[] case4 = {1, 2, 3, 4, 5};
        int K4 = 7;
        System.out.println("테스트 4: " + K4 + "번 회전");
        test.printArray(test.solution(case4, K4)); // 예상: [4, 5, 1, 2, 3]

        // 테스트 케이스 5 (빈 배열)
        int[] case5 = {};
        int K5 = 10;
        System.out.println("테스트 5: 빈 배열");
        test.printArray(test.solution(case5, K5)); // 예상: []
    }

}
