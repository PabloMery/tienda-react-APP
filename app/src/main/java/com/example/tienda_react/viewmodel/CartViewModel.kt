package com.example.tienda_react.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tienda_react.data.FakeData
import com.example.tienda_react.data.users.UserRepository
import com.example.tienda_react.domain.CartItem
import com.example.tienda_react.domain.CartItemRequest
import com.example.tienda_react.domain.CartResponse
import com.example.tienda_react.domain.Product
import com.example.tienda_react.network.CartRetrofitClient
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
    val error: String? = null
)

class CartViewModel(app: Application) : AndroidViewModel(app) {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui

    // Obtenemos el ID del usuario logueado desde la sesión local.
    // Si es null (invitado), algunas funciones no harán nada.
    private val currentUserId: Long?
        get() = UserRepository.SessionManager.currentUser?.id

    init {
        // Al iniciar la pantalla, intentamos traer el carrito del servidor
        fetchCart()
    }

    /**
     * Obtiene el carrito del servidor (GET /api/cart)
     */
    fun fetchCart() = viewModelScope.launch {
        val userId = currentUserId ?: return@launch
        try {
            val response = CartRetrofitClient.api.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                updateUiFromResponse(response.body()!!)
            } else {
                Log.e("CartViewModel", "Error fetchCart: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Excepción fetchCart", e)
        }
    }

    /**
     * Agrega un producto al carrito (POST /api/cart/items)
     * Siempre enviamos quantity=1 para que el backend sume.
     */
    fun add(p: Product) = viewModelScope.launch {
        val userId = currentUserId
        if (userId == null) {
            // Manejo básico para usuario invitado (opcional: podrías mostrar un error)
            Log.w("CartViewModel", "Usuario no logueado, no se puede agregar al carrito remoto.")
            return@launch
        }

        try {
            val request = CartItemRequest(productId = p.id.toLong(), quantity = 1)
            val response = CartRetrofitClient.api.addItem(userId, request)

            if (response.isSuccessful && response.body() != null) {
                updateUiFromResponse(response.body()!!)
            } else {
                Log.e("CartViewModel", "Error add: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Excepción add", e)
        }
    }

    /**
     * Actualiza la cantidad de un ítem.
     * Si qty <= 0, lo eliminamos.
     */
    fun setQty(productId: Int, newQty: Int) = viewModelScope.launch {
        val userId = currentUserId ?: return@launch

        try {
            if (newQty <= 0) {
                // Si la cantidad es 0 o menor, llamamos al endpoint DELETE
                remove(productId)
            } else {
                // Si es mayor a 0, llamamos al endpoint PUT (actualizar cantidad absoluta)
                val request = CartItemRequest(productId = productId.toLong(), quantity = newQty)
                val response = CartRetrofitClient.api.updateItem(userId, request)

                if (response.isSuccessful && response.body() != null) {
                    updateUiFromResponse(response.body()!!)
                } else {
                    Log.e("CartViewModel", "Error setQty: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Excepción setQty", e)
        }
    }

    /**
     * Elimina un ítem del carrito (DELETE /api/cart/items/{id})
     */
    fun remove(productId: Int) = viewModelScope.launch {
        val userId = currentUserId ?: return@launch
        try {
            val response = CartRetrofitClient.api.removeItem(userId, productId.toLong())
            if (response.isSuccessful && response.body() != null) {
                updateUiFromResponse(response.body()!!)
            } else {
                Log.e("CartViewModel", "Error remove: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Excepción remove", e)
        }
    }

    /**
     * Vacía el carrito completo (DELETE /api/cart)
     */
    fun clearCart() = viewModelScope.launch {
        val userId = currentUserId ?: return@launch
        try {
            val response = CartRetrofitClient.api.clearCart(userId)
            if (response.isSuccessful && response.body() != null) {
                updateUiFromResponse(response.body()!!)
            } else {
                Log.e("CartViewModel", "Error clearCart: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Excepción clearCart", e)
        }
    }

    /**
     * Convierte la respuesta del servidor (IDs y cantidades) en objetos completos
     * para la UI (con Nombre, Precio e Imagenes desde FakeData).
     */
    private fun updateUiFromResponse(cartResponse: CartResponse) {
        // Mapeamos cada item que viene del servidor
        val mappedItems = cartResponse.items.mapNotNull { itemResp ->
            // Buscamos los detalles visuales en nuestra data local (FakeData)
            val localProduct = FakeData.PRODUCTS.find { it.id == itemResp.productId.toInt() }

            if (localProduct != null) {
                CartItem(
                    id = itemResp.productId.toInt(),
                    product = localProduct,
                    qty = itemResp.quantity
                )
            } else {
                // Si el producto no existe en la app (FakeData), lo ignoramos
                null
            }
        }

        // Recalculamos totales basados en los items mapeados
        val totalItems = mappedItems.sumOf { it.qty }
        val totalPrice = mappedItems.sumOf { it.qty * it.product.price }

        // Mantenemos la lógica de cupones actual (visual) sobre el nuevo subtotal
        val (discount, err) = computeDiscount(totalPrice, _ui.value.couponCode)
        val totalAfter = (totalPrice - discount).coerceAtLeast(0)

        _ui.value = _ui.value.copy(
            items = mappedItems,
            totalItems = totalItems,
            totalPrice = totalPrice,
            discount = discount,
            couponError = err,
            totalAfterDiscount = totalAfter
        )
    }

    // ---------------------------
    // 💸 Lógica de cupones (Local)
    // ---------------------------
    // Esta lógica se mantiene localmente para efectos visuales, ya que el backend
    // aún no procesa cupones. Se aplica sobre el total calculado.

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