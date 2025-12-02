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
            try {
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
            } catch (e: Exception) {
                Log.e("CartViewModel", "Crash en loadCart", e)
                _ui.value = _ui.value.copy(isLoading = false, errorMsg = "Error inesperado al cargar")
            }
        }
    }

    // Agregar producto (desde el botón "Añadir al carrito" en detalle)
    fun add(p: Product) = viewModelScope.launch {
        val pid = p.id ?: run {
            Log.e("CartViewModel", "Intento de agregar producto sin ID")
            return@launch
        }

        val userId = UserRepository.SessionManager.getUserId()
        _ui.value = _ui.value.copy(isLoading = true)

        try {
            // Llama a addItem del Repo (POST)
            val result = CartRepository.addItem(userId, pid, 1)

            if (result.isSuccess) {
                val newItems = result.getOrDefault(emptyList())
                updateUi(newItems)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Error al agregar"
                Log.e("CartViewModel", err)
                _ui.value = _ui.value.copy(isLoading = false, errorMsg = err)
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Crash en add", e)
            _ui.value = _ui.value.copy(isLoading = false, errorMsg = "Error crítico al agregar: ${e.message}")
        }
    }

    // Modificar cantidad (Botones + y - en el carrito) [ESTO ES LO CORREGIDO]
    fun setQty(id: Long, qty: Int) = viewModelScope.launch {
        val userId = UserRepository.SessionManager.getUserId()

        // 1. Si la cantidad es 0 o menor, borramos el ítem usando remove
        if (qty <= 0) {
            remove(id)
            return@launch
        }

        _ui.value = _ui.value.copy(isLoading = true)

        try {
            // 2. Llamamos a la nueva función updateQuantity (PUT) del repositorio
            val result = CartRepository.updateQuantity(userId, id, qty)

            if (result.isSuccess) {
                // 3. Actualizamos la UI con el carrito actualizado que devuelve el backend
                val newItems = result.getOrDefault(emptyList())
                updateUi(newItems)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Error al actualizar cantidad"
                _ui.value = _ui.value.copy(isLoading = false, errorMsg = err)
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Crash en setQty", e)
            _ui.value = _ui.value.copy(isLoading = false, errorMsg = "Error inesperado")
        }
    }

    // Eliminar ítem (Papelera)
    fun remove(productId: Long) = viewModelScope.launch {
        val userId = UserRepository.SessionManager.getUserId()
        try {
            val result = CartRepository.removeItem(userId, productId)

            if (result.isSuccess) {
                updateUi(result.getOrDefault(emptyList()))
            } else {
                _ui.value = _ui.value.copy(errorMsg = "Error eliminando")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Crash en remove", e)
        }
    }

    private fun updateUi(items: List<CartItem>) {
        try {
            // Protección contra items incompletos
            val totalItems = items.sumOf { it.qty }

            val totalPrice = items.sumOf {
                try {
                    it.qty * it.product.price
                } catch (e: NullPointerException) {
                    Log.w("CartViewModel", "Item sin producto o precio: $it")
                    0
                }
            }

            // Recalcular descuentos si hay cupón activo
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
        } catch (e: Exception) {
            Log.e("CartViewModel", "Error calculando totales UI", e)
            _ui.value = _ui.value.copy(isLoading = false, errorMsg = "Error visualizando carrito")
        }
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