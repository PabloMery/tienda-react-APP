package com.example.tienda_react.data.users

import androidx.compose.runtime.mutableStateListOf
import com.example.tienda_react.domain.User
import kotlinx.coroutines.delay

/**
 * Repositorio en memoria para la actividad (sin DB).
 * - register(): valida duplicados por correo
 * - login(): valida credenciales exactas
 * - seed(): opcional, para crear 1 usuario de prueba
 */
object UserRepository {

    private val users = mutableStateListOf<User>()
    private var nextId = 1

    init {
        // Opcional: un usuario semilla para probar login rápido
        // seed("Alumno DUOC", "demo@duoc.cl", "123456")
    }

    object SessionManager {
        var currentUser: User? = null
    }
    fun isEmailAllowed(email: String): Boolean {
        // Acepta duoc.cl, profesor.duoc.cl y gmail.com
        val rx = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$", RegexOption.IGNORE_CASE)
        return rx.matches(email.trim())
    }

    fun emailExists(email: String): Boolean =
        users.any { it.correo.equals(email.trim(), ignoreCase = true) }

    suspend fun register(
        nombre: String,
        correo: String,
        contrasena: String,
        telefono: String?,
        region: String,
        comuna: String
    ): Result<Unit> {
        // Simulamos latencia de red (opcional)
        delay(300)

        if (!isEmailAllowed(correo)) {
            return Result.failure(IllegalArgumentException("Correo no permitido"))
        }
        if (emailExists(correo)) {
            return Result.failure(IllegalArgumentException("El correo ya está registrado"))
        }
        val user = User(
            id = nextId++,
            nombre = nombre.trim(),
            correo = correo.trim(),
            contrasena = contrasena, // Para la actividad no encriptamos
            telefono = telefono?.ifBlank { null },
            region = region,
            comuna = comuna
        )
        users += user
        return Result.success(Unit)
    }

    suspend fun login(email: String, password: String): Result<User> {
        delay(200)
        val u = users.firstOrNull {
            it.correo.equals(email.trim(), ignoreCase = true) && it.contrasena == password
        } ?: return Result.failure(IllegalArgumentException("Credenciales inválidas"))
        SessionManager.currentUser = u
        return Result.success(u)
    }

    // Utilidad opcional para pruebas manuales
    fun seed(nombre: String, correo: String, pass: String) {
        if (!emailExists(correo)) {
            users += User(
                id = nextId++,
                nombre = nombre,
                correo = correo,
                contrasena = pass,
                telefono = null,
                region = "Metropolitana de Santiago",
                comuna = "Santiago"
            )
        }
    }
}
