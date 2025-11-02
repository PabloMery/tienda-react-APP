
package com.example.tienda_react.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tienda_react.data.cart.CartStore
import com.example.tienda_react.domain.CartItem
import com.example.tienda_react.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Int = 0,
    val couponCode: String? = null,
    val discount: Int = 0,
    val couponError: String? = null,
    val totalAfterDiscount: Int = 0
)

class CartViewModel(app: Application) : AndroidViewModel(app) {

    private val store = CartStore(app)

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    init {
        viewModelScope.launch {
            val loaded = store.read()
            _ui.value = calc(loaded)
        }
    }

    /** Añadir 1 unidad del producto al carrito */
    fun add(p: Product) = viewModelScope.launch {
        val cur = _ui.value.items
        val ex = cur.find { it.id == p.id }
        val newItems =
            if (ex == null) cur + CartItem(p.id, p, 1)
            else cur.map { if (it.id == p.id) it.copy(qty = it.qty + 1) else it }
        persist(newItems)
    }

    /** Fijar cantidad (si llega 0, se elimina) */
    fun setQty(id: Int, qty: Int) = viewModelScope.launch {
        val newItems = _ui.value.items
            .map { if (it.id == id) it.copy(qty = qty.coerceAtLeast(0)) else it }
            .filter { it.qty > 0 }
        persist(newItems)
    }

    /** Eliminar ítem del carrito */
    fun remove(id: Int) = viewModelScope.launch {
        val newItems = _ui.value.items.filter { it.id != id }
        persist(newItems)
    }

    /** Guardar en DataStore y actualizar UI */
    private suspend fun persist(items: List<CartItem>) {
        store.write(items)
        _ui.value = calc(items, _ui.value.couponCode)
    }

    /** Recalcular totales + cupones */
    private fun calc(items: List<CartItem>, coupon: String? = null): CartUiState {
        val totalItems = items.sumOf { it.qty }
        val totalPrice = items.sumOf { it.qty * it.product.price }

        val (discount, err) = computeDiscount(totalPrice, coupon)
        val totalAfter = (totalPrice - discount).coerceAtLeast(0)

        return CartUiState(
            items = items,
            totalItems = totalItems,
            totalPrice = totalPrice,
            couponCode = coupon,
            discount = discount,
            couponError = err,
            totalAfterDiscount = totalAfter
        )
    }

    // ---------------------------
    // 💸 Lógica de cupones
    // ---------------------------
    private val percentCoupons = mapOf(
        "SAVE10" to 10,
        "SAVE20" to 20
    )
    private val fixedCoupons = mapOf(
        "FIX5000" to 5000,
        "FIX10000" to 10000
    )

    private fun computeDiscount(total: Int, code: String?): Pair<Int, String?> {
        if (code.isNullOrBlank()) return 0 to null
        val normalized = code.trim().uppercase()

        percentCoupons[normalized]?.let {
            val d = (total * (it / 100.0)).roundToInt()
            return d to null
        }

        fixedCoupons[normalized]?.let {
            val d = it.coerceAtMost(total)
            return d to null
        }

        return 0 to "Cupón no válido"
    }

    fun applyCoupon(code: String) {
        val (discount, err) = computeDiscount(_ui.value.totalPrice, code)
        val totalAfter = (_ui.value.totalPrice - discount).coerceAtLeast(0)

        _ui.value = _ui.value.copy(
            couponCode = code.trim().uppercase(),
            discount = discount,
            couponError = err,
            totalAfterDiscount = totalAfter
        )
    }

    fun clearCoupon() {
        _ui.value = _ui.value.copy(
            couponCode = null,
            discount = 0,
            couponError = null,
            totalAfterDiscount = _ui.value.totalPrice
        )
    }
}
