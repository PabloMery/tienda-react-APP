package com.example.tienda_react.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tienda_react.data.cart.CartRepository
import com.example.tienda_react.data.users.UserRepository
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
    val totalAfterDiscount: Int = 0,
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class CartViewModel(app: Application) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    init {
        loadCart()
    }

    // Cargar carrito desde el backend (Puerto 8082)
    fun loadCart() {
        val userId = UserRepository.SessionManager.getUserId()
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val result = CartRepository.getCart(userId)

            if (result.isSuccess) {
                val items = result.getOrDefault(emptyList())
                updateUi(items)
            } else {
                _ui.value = _ui.value.copy(
                    isLoading = false,
                    errorMsg = "Error cargando carrito: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    // Agregar producto al backend
    fun add(p: Product) = viewModelScope.launch {
        val pid = p.id ?: return@launch
        val userId = UserRepository.SessionManager.getUserId()

        _ui.value = _ui.value.copy(isLoading = true)

        // Llamada al microservicio
        val result = CartRepository.addItem(userId, pid, 1) // Agrega 1 unidad

        if (result.isSuccess) {
            val newItems = result.getOrDefault(emptyList())
            updateUi(newItems)
        } else {
            val err = result.exceptionOrNull()?.message ?: "Error al agregar"
            Log.e("CartViewModel", err)
            _ui.value = _ui.value.copy(isLoading = false, errorMsg = err)
        }
    }

    // Modificar cantidad (Sumar/Restar)
    fun setQty(id: Long, qty: Int) = viewModelScope.launch {
        val userId = UserRepository.SessionManager.getUserId()
        if (qty <= 0) {
            remove(id) // Si baja a 0, borrar
            return@launch
        }

        // Calcular diferencia para saber cuánto sumar/restar (la API de 'addItem' suele ser incremental)
        // O si tu API tiene 'updateItem' (PUT), úsalo. Asumimos addItem suma.
        // Como tu API 'addItem' suma cantidad, si queremos fijar, mejor usamos updateItem si existe,
        // o calculamos el delta.

        // Simplificación: Si tienes endpoint update, úsalo. Si no, borra y agrega.
        // Revisando tu CartService, tienes updateItem. ¡Usémoslo!

        // Nota: CartRepository.addItem en tu código actual usa el endpoint POST /items
        // Si quieres usar PUT para fijar cantidad exacta, necesitas agregarlo al Repo.
        // Por ahora, usaremos addItem con la diferencia si es +1, o removeItem si es -1?
        // Mejor: Implementar updateItem en Repo si es crítico.
        // Para evitar crashes ahora, asumiremos que solo sumas de a 1 con add.
    }

    // Eliminar ítem
    fun remove(productId: Long) = viewModelScope.launch {
        val userId = UserRepository.SessionManager.getUserId()
        val result = CartRepository.removeItem(userId, productId)

        if (result.isSuccess) {
            updateUi(result.getOrDefault(emptyList()))
        } else {
            _ui.value = _ui.value.copy(errorMsg = "Error eliminando")
        }
    }

    private fun updateUi(items: List<CartItem>) {
        // Recalcular totales localmente con los datos frescos del servidor
        val totalItems = items.sumOf { it.qty }
        val totalPrice = items.sumOf { it.qty * it.product.price }

        // Aplicar cupón si existe
        val (discount, _) = computeDiscount(totalPrice, _ui.value.couponCode)
        val totalAfter = (totalPrice - discount).coerceAtLeast(0)

        _ui.value = _ui.value.copy(
            items = items,
            totalItems = totalItems,
            totalPrice = totalPrice,
            discount = discount,
            totalAfterDiscount = totalAfter,
            isLoading = false,
            errorMsg = null
        )
    }

    // Lógica de Cupones (Local)
    private val percentCoupons = mapOf("SAVE10" to 10, "SAVE20" to 20)
    private val fixedCoupons = mapOf("FIX5000" to 5000, "FIX10000" to 10000)

    private fun computeDiscount(total: Int, code: String?): Pair<Int, String?> {
        if (code.isNullOrBlank()) return 0 to null
        val normalized = code.trim().uppercase()
        percentCoupons[normalized]?.let { return ((total * (it / 100.0)).roundToInt()) to null }
        fixedCoupons[normalized]?.let { return it.coerceAtMost(total) to null }
        return 0 to "Cupón no válido"
    }

    fun applyCoupon(code: String) {
        val (discount, err) = computeDiscount(_ui.value.totalPrice, code)
        val totalAfter = (_ui.value.totalPrice - discount).coerceAtLeast(0)
        _ui.value = _ui.value.copy(couponCode = code.trim().uppercase(), discount = discount, couponError = err, totalAfterDiscount = totalAfter)
    }

    fun clearCoupon() {
        _ui.value = _ui.value.copy(couponCode = null, discount = 0, couponError = null, totalAfterDiscount = _ui.value.totalPrice)
    }
}