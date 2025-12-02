package com.example.tienda_react.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // --- TUS PUERTOS REALES ---
    // Productos (Spring Boot default): 8080
    private const val BASE_URL_PRODUCTOS = "http://10.0.2.2:8080/"
    // Usuarios (según tu application.properties): 8081
    private const val BASE_URL_USUARIOS = "http://10.0.2.2:8081/"
    // Carrito (según tu application.properties): 8082
    private const val BASE_URL_CARRITO = "http://10.0.2.2:8082/"

    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // --- AQUÍ ESTÁ LA MAGIA QUE ARREGLA EL ERROR ---

    // 1. Conexión a Productos (8080)
    val productsApi: ApiService by lazy {
        buildRetrofit(BASE_URL_PRODUCTOS).create(ApiService::class.java)
    }

    // 2. Conexión a Usuarios (8081)
    val usersApi: ApiService by lazy {
        buildRetrofit(BASE_URL_USUARIOS).create(ApiService::class.java)
    }

    // 3. Conexión a Carrito (8082)
    val cartApi: ApiService by lazy {
        buildRetrofit(BASE_URL_CARRITO).create(ApiService::class.java)
    }
}