@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tienda_react.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tienda_react.data.users.UserRepository
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    onGoRegistro: () -> Unit,
    onLoginOk: (/*User?*/ ) -> Unit
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Iniciar sesión") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth()
        ) {
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo (duoc.cl / profesor.duoc.cl / gmail.com)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = pass, onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                enabled = !loading,
                onClick = {
                    error = null; info = null
                    if (!UserRepository.isEmailAllowed(email)) {
                        error = "Correo no permitido"
                        return@Button
                    }
                    if (pass.isBlank()) {
                        error = "Ingresa tu contraseña"
                        return@Button
                    }
                    loading = true
                    scope.launch {
                        val r = UserRepository.login(email, pass)
                        loading = false
                        if (r.isSuccess) {
                            info = "¡Bienvenido!"
                            onLoginOk(/*r.getOrNull()*/)
                        } else {
                            error = r.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Ingresando..." else "Ingresar") }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onGoRegistro, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Regístrate")
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}