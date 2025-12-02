package com.example.tienda_react.data.cart

import android.util.Log
import com.example.tienda_react.domain.CartItem
import com.example.tienda_react.domain.CartItemRequest
import com.example.tienda_react.network.RetrofitClient

object CartRepository {

    // Todas las llamadas usan .cartApi (8082)

    suspend fun getCart(userId: Long): Result<List<CartItem>> {
        return try {
            val response = RetrofitClient.cartApi.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Error carrito: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Error getCart", e)
            Result.failure(e)
        }
    }

    suspend fun addItem(userId: Long, productId: Long, quantity: Int): Result<List<CartItem>> {
        return try {
            val req = CartItemRequest(productId, quantity)
            // Llamamos a addItemToCart definido en ApiService
            val response = RetrofitClient.cartApi.addItemToCart(userId, req)

            if (response.isSuccessful && response.body() != null) {
                // El backend devuelve el carrito completo actualizado
                Result.success(response.body()!!.items)
            } else {
                // Log para ver qué falló (ej: 404 product not found en el microservicio de carrito)
                val errorBody = response.errorBody()?.string()
                Log.e("CartRepo", "Error addItem: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al agregar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CartRepo", "Excepción addItem", e)
            Result.failure(e)
        }
    }

    suspend fun removeItem(userId: Long, productId: Long): Result<List<CartItem>> {
        return try {
            val response = RetrofitClient.cartApi.removeItemFromCart(userId, productId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.items)
            } else {
                Result.failure(Exception("Error remove item: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}