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
