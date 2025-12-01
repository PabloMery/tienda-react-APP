package com.example.tienda_react.data.users

import android.util.Log
import com.example.tienda_react.domain.User
import com.example.tienda_react.network.LoginRequestDto
import com.example.tienda_react.network.RetrofitClient

object UserRepository {

    object SessionManager {
        var currentUser: User? = null
    }

    // Validación local del correo
    fun isEmailAllowed(email: String): Boolean {
        val rx = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
        return rx.matches(email.trim())
    }

    // --- LOGIN ---
    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val request = LoginRequestDto(correo = email, pass = pass)
            val response = RetrofitClient.api.login(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                SessionManager.currentUser = user
                Result.success(user)
            } else {
                Log.e("ERROR_API", "Fallo Login: ${response.code()}")
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Log.e("ERROR_API", "Excepción Login", e)
            Result.failure(Exception("Error de conexión con el servidor"))
        }
    }

    // --- REGISTRO ---
    suspend fun register(
        nombre: String, correo: String, contrasena: String,
        telefono: String?, region: String, comuna: String
    ): Result<Unit> {

        // CORRECCIÓN AQUÍ:
        // Asignamos id = null para que Hibernate (Java) sepa que es un INSERT nuevo
        val userToSend = User(
            id = null,
            nombre = nombre,
            correo = correo,
            contrasena = contrasena,
            telefono = telefono,
            region = region,
            comuna = comuna
        )

        Log.d("DEBUG_API", "Enviando usuario nuevo: $userToSend")

        return try {
            val response = RetrofitClient.api.register(userToSend)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("ERROR_API", "Fallo Registro. Código: ${response.code()} Body: $errorBody")

                if (response.code() == 409) {
                    Result.failure(Exception("El correo ya está registrado"))
                } else {
                    Result.failure(Exception("Error del servidor: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR_API", "Excepción Registro", e)
            Result.failure(Exception("No se pudo conectar al servidor"))
        }
    }
}

