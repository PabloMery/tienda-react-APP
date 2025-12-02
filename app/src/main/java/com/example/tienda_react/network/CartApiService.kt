package com.example.tienda_react.network

import com.example.tienda_react.domain.CartItemRequest
import com.example.tienda_react.domain.CartResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CartApiService {
    @GET("api/cart")
    suspend fun getCart(@Header("X-User-Id") userId: Long): Response<CartResponse>

    @POST("api/cart/items")
    suspend fun addItem(
        @Header("X-User-Id") userId: Long,
        @Body item: CartItemRequest
    ): Response<CartResponse>

    @PUT("api/cart/items")
    suspend fun updateItem(
        @Header("X-User-Id") userId: Long,
        @Body item: CartItemRequest
    ): Response<CartResponse>

    @DELETE("api/cart/items/{productId}")
    suspend fun removeItem(
        @Header("X-User-Id") userId: Long,
        @Path("productId") productId: Long
    ): Response<CartResponse>

    @DELETE("api/cart")
    suspend fun clearCart(@Header("X-User-Id") userId: Long): Response<CartResponse>
}

// 2. El objeto Cliente exclusivo para el puerto 8082
object CartRetrofitClient {
    // Apuntamos SOLO este cliente a tu microservicio
    private const val BASE_URL = "http://10.0.2.2:8082/"

    val api: CartApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartApiService::class.java)
    }
}