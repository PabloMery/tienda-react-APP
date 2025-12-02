package com.example.tienda_react.data.products

import android.util.Log
import com.example.tienda_react.domain.Product
import com.example.tienda_react.network.RetrofitClient

object ProductRepository {

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val response = RetrofitClient.api.getProductos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar productos: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("API_PRODUCTOS", "Error conexión", e)
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<Product> {
        return try {
            val response = RetrofitClient.api.getProductoById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Producto no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- NUEVAS FUNCIONES ---

    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val response = RetrofitClient.api.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.api.deleteProduct(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}