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
