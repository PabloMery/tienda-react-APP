package com.example.tienda_react.network

import com.example.tienda_react.domain.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Login: Coincide con tu Controller Java @PostMapping("/login")
    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestDto): Response<User>

    // Registro: Coincide con tu Controller Java @PostMapping (raíz)
    @POST("api/usuarios")
    suspend fun register(@Body user: User): Response<User>
}

// Clase DTO para enviar solo correo y pass en el login
data class LoginRequestDto(
    val correo: String,
    val pass: String
)