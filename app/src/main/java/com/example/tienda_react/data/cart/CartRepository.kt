package com.example.tienda_react.data.cart

import android.util.Log
import com.example.tienda_react.data.products.ProductRepository
import com.example.tienda_react.domain.CartItem
import com.example.tienda_react.domain.CartItemBackend
import com.example.tienda_react.domain.CartItemRequest
import com.example.tienda_react.domain.Product
import com.example.tienda_react.network.CartRetrofitClient

object CartRepository {

    // --- FUNCIÓN MÁGICA ---
    // Recibe la lista de IDs del servidor de Carrito (8082)
    // Y busca los detalles en el servidor de Productos (8080)
    private suspend fun mapBackendResponseToUi(backendItems: List<CartItemBackend>?): List<CartItem> {
        if (backendItems == null) return emptyList()

        val uiItems = mutableListOf<CartItem>()

        for (item in backendItems) {
            // 1. Buscamos el detalle completo del producto usando el ID
            val productResult = ProductRepository.getProductById(item.productId)

            // 2. Verificamos si la búsqueda fue exitosa
            val realProduct = if (productResult.isSuccess) {
                productResult.getOrThrow()
            } else {
                // Fallback: Si falla la conexión con productos, mostramos algo genérico
                // para que el carrito no se rompa.
                Log.e("CartRepo", "No se pudo cargar producto ID: ${item.productId}")
                Product(
                    id = item.productId,
                    name = "Producto no disponible",
                    price = 0,
                    category = "Sin categoría",
                    stock = 0,
                    images = null
                )
            }

            // 3. Creamos el ítem final para la UI
            uiItems.add(
                CartItem(
                    id = item.productId,
                    product = realProduct, // ¡Aquí va la imagen y datos reales!
                    qty = item.quantity
                )
            )
        }
        return uiItems
    }

    // --- OBTENER CARRITO (GET) ---
    suspend fun getCart(userId: Long): Result<List<CartItem>> {
        return try {
            val response = CartRetrofitClient.api.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                val uiItems = mapBackendResponseToUi(response.body()!!.items)
                Result.success(uiItems)
            } else {
                Result.failure(Exception("Error al cargar carrito: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Error getCart", e)
            Result.failure(e)
        }
    }

    // --- AGREGAR ITEM (POST) ---
    suspend fun addItem(userId: Long, productId: Long, quantity: Int): Result<List<CartItem>> {
        return try {
            val req = CartItemRequest(productId, quantity)
            val response = CartRetrofitClient.api.addItem(userId, req)

            if (response.isSuccessful && response.body() != null) {
                val uiItems = mapBackendResponseToUi(response.body()!!.items)
                Result.success(uiItems)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CartRepo", "Error addItem: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al agregar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Excepción addItem", e)
            Result.failure(e)
        }
    }

    // --- ACTUALIZAR CANTIDAD (PUT) --- [ESTO ES LO NUEVO QUE NECESITAS]
    suspend fun updateQuantity(userId: Long, productId: Long, quantity: Int): Result<List<CartItem>> {
        return try {
            val req = CartItemRequest(productId, quantity)
            // Llamamos al endpoint PUT definido en CartApiService
            val response = CartRetrofitClient.api.updateItem(userId, req)

            if (response.isSuccessful && response.body() != null) {
                val uiItems = mapBackendResponseToUi(response.body()!!.items)
                Result.success(uiItems)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("CartRepo", "Error updateItem: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al actualizar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Excepción updateItem", e)
            Result.failure(e)
        }
    }

    // --- ELIMINAR ITEM (DELETE) ---
    suspend fun removeItem(userId: Long, productId: Long): Result<List<CartItem>> {
        return try {
            val response = CartRetrofitClient.api.removeItem(userId, productId)
            if (response.isSuccessful && response.body() != null) {
                val uiItems = mapBackendResponseToUi(response.body()!!.items)
                Result.success(uiItems)
            } else {
                Result.failure(Exception("Error remove item: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}