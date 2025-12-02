package com.example.tienda_react.network

import com.example.tienda_react.domain.Product
import com.example.tienda_react.domain.User
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- USUARIOS ---
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestDto): Response<User>

    @POST("api/usuarios")
    suspend fun register(@Body user: User): Response<User>

    // --- PRODUCTOS ---
    @GET("api/productos")
    suspend fun getProductos(): Response<List<Product>>

    @GET("api/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<Product>

    @POST("api/productos")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Unit>

    // --- ARCHIVOS ---
    @Multipart
    @POST("api/files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ResponseBody>
}

data class LoginRequestDto(
    val correo: String,
    val pass: String
)