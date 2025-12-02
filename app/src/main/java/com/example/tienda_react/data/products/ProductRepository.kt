package com.example.tienda_react.data.products

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.tienda_react.domain.Product
import com.example.tienda_react.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object ProductRepository {

    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            // CORRECCIÓN: Llamamos al puerto 8080
            val response = RetrofitClient.productsApi.getProductos()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("ProductRepo", "Error al leer productos", e)
            Result.failure(e)
        }
    }

    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val response = RetrofitClient.productsApi.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error creando: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: Long): Result<Unit> {
        return try {
            val response = RetrofitClient.productsApi.deleteProduct(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error eliminando: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes == null) return Result.failure(Exception("No se pudo leer imagen"))

            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)

            // Subimos la imagen al servicio de Productos (FileController está ahí)
            val response = RetrofitClient.productsApi.uploadImage(body)

            if (response.isSuccessful) Result.success(response.body()?.string() ?: "")
            else Result.failure(Exception("Fallo upload: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: Long): Result<Product> {
        return try {
            val response = RetrofitClient.productsApi.getProductoById(id)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("No encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}