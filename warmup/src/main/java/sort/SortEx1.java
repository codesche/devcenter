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
