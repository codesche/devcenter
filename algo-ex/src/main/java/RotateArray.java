
// 정수 배열 A, 정수 K 주어짐
// K번 오른쪽으로 회전한 배열을 반환.

// A = [3, 8, 9, 7, 6], K = 3
// => [9, 7, 6, 3, 8]

/**
 * 핵심 패턴:
 *
 * 인덱스 계산 시 (i + K) % N 활용
 * 회전 수 K가 배열 크기보다 클 수 있으므로 'K % N' 으로 줄여서 계산
 * 배열 복사보다 새 배열 생성 후 매핑이 안전하고 직관적
 */

/**
 * 시간복잡도: for 루프, O(N) + mod 연산, O(1) => 전체 시간 복잡도: O(N)
 * 공간복잡도: 입력 배열 A 그대로 유지, 새로운 배열 rotated를 크기 N만큼 추가로 생성 => O(N)
 */
public class RotateArray {

    public int[] solution(int[] A, int K) {
        int N = A.length;
        if (N == 0) {
            return A;
        }

        int[] rotated = new int[N];
        K %= N;                     // 불필요한 회전 줄이기 (K가 배열 크기보다 큰 경우를 고려)

        for (int i = 0; i < N; i++) {           // O(N)
            int newIndex = (i + K) % N;         // O(1)
            rotated[newIndex] = A[i];
        }

        return rotated;
    }

}
