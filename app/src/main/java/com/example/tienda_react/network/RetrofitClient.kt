package com.example.tienda_react.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 es la IP especial para que el emulador acceda al localhost de tu PC.
    // 8080 es el puerto definido en tu application.properties de Spring Boot.
    private const val BASE_URL = "http://10.0.2.2:8081/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}