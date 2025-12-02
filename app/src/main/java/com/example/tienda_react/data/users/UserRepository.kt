package com.example.tienda_react.data.users

import android.util.Log
import com.example.tienda_react.domain.User
import com.example.tienda_react.network.LoginRequestDto
import com.example.tienda_react.network.RetrofitClient

object UserRepository {

    object SessionManager {
        var currentUser: User? = null
        fun getUserId(): Long = currentUser?.id ?: 1L // ID temporal si no hay login
    }

    fun isEmailAllowed(email: String) = email.contains("@")

    // LOGIN -> Puerto 8081
    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val request = LoginRequestDto(correo = email, pass = pass)

            // USAMOS usersApi
            val response = RetrofitClient.usersApi.login(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                SessionManager.currentUser = user
                Result.success(user)
            } else {
                Log.e("UserRepo", "Login fallido: ${response.code()}")
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Log.e("UserRepo", "Error login", e)
            Result.failure(Exception("Error de conexión"))
        }
    }

    // REGISTRO -> Puerto 8081
    suspend fun register(
        nombre: String, correo: String, contrasena: String,
        telefono: String?, region: String, comuna: String
    ): Result<Unit> {
        val user = User(null, nombre, correo, contrasena, telefono, region, comuna)
        return try {
            // USAMOS usersApi
            val response = RetrofitClient.usersApi.register(user)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error registro: ${response.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}