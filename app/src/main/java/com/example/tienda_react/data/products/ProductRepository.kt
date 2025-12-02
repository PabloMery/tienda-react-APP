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

    // ... (getAllProducts, getProductById se mantienen igual) ...
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

    // --- NUEVA FUNCIÓN: Subir Imagen desde URI ---
    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> {
        return try {
            // 1. Leer los bytes del archivo seleccionado
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: return Result.failure(Exception("No se pudo leer la imagen"))
            inputStream.close()

            // 2. Crear el cuerpo de la petición Multipart
            val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", "upload.jpg", requestFile)

            // 3. Enviar al servidor
            val response = RetrofitClient.api.uploadImage(body)

            if (response.isSuccessful) {
                // El servidor devuelve la URL relativa (ej: "/api/files/uuid_foto.jpg")
                val url = response.body()?.string() ?: ""
                Result.success(url)
            } else {
                Result.failure(Exception("Fallo al subir imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}