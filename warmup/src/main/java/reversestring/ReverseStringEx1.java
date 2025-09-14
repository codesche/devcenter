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
