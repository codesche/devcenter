
// 나쁜 예 - 외부에서 마음대로 변경 가능
class BadShoppingCart {
    val items = mutableListOf<Item>()       // 위험!
}

val cart = BadShoppingCart()
//cart.items.clear() // 외부에서 장바구니를 비워버릴 수 있다

// ✅ 좋은 예 - 변경을 통제
class GoodShoppingCart {
    private val _items = mutableListOf<Item>()
    val items: List<Item> get() = _items.toList() // 읽기 전용으로 노출

    fun addItem(item: Item) {
        _items.add(item)
    }

    fun removeItem(item: Item) {
        _items.remove(item)
    }
}

class Item {

}