package com.example.tienda_react.network

import com.example.tienda_react.domain.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============================
    // MICROSERVICIO USUARIOS (8081)
    // ============================
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestDto): Response<User>

    @POST("api/usuarios")
    suspend fun register(@Body user: User): Response<User>

    // ============================
    // MICROSERVICIO PRODUCTOS (8080)
    // ============================
    @GET("api/productos")
    suspend fun getProductos(): Response<List<Product>>

    @GET("api/productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): Response<Product>

    @POST("api/productos")
    suspend fun createProduct(@Body product: Product): Response<Product>

    @DELETE("api/productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): Response<ResponseBody>

    // ============================
    // MICROSERVICIO CARRITO (8082)
    // ============================
    @GET("api/cart")
    suspend fun getCart(@Header("X-User-Id") userId: Long): Response<CartResponseBackend>

    @POST("api/cart/items")
    suspend fun addItemToCart(
        @Header("X-User-Id") userId: Long,
        @Body item: CartItemRequest
    ): Response<CartResponseBackend>

    @DELETE("api/cart/items/{productId}")
    suspend fun removeItemFromCart(
        @Header("X-User-Id") userId: Long,
        @Path("productId") productId: Long
    ): Response<CartResponseBackend>

    @DELETE("api/cart")
    suspend fun clearCart(@Header("X-User-Id") userId: Long): Response<CartResponseBackend>
}

// DTO para Login
data class LoginRequestDto(val correo: String, val pass: String)