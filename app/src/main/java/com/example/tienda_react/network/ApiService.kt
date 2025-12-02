package com.example.tienda_react.network

import com.example.tienda_react.domain.Product
import com.example.tienda_react.domain.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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

    // NUEVOS ENDPOINTS PARA ADMIN
    @POST("api/productos")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Unit>
}

data class LoginRequestDto(
    val correo: String,
    val pass: String
)