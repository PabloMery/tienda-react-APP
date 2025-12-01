@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tienda_react.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tienda_react.data.users.UserRepository
import com.example.tienda_react.utils.ChileData
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(
    onBackLogin: () -> Unit,
    onRegistroOk: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // ESTADOS PARA DROPDOWNS
    var region by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }

    // 1. OBTENER DATOS
    val mapRegiones = ChileData.regionesYComunas

    // 2. LISTA DE REGIONES
    val listaRegiones = remember { mapRegiones.keys.toList() }

    // 3. LISTA DE COMUNAS
    val listaComunas = remember(region) {
        mapRegiones[region] ?: emptyList()
    }

    var showRegiones by remember { mutableStateOf(false) }
    var showComunas by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var ok by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun validar(): String? {
        if (nombre.isBlank()) return "Ingresa tu nombre"
        if (!UserRepository.isEmailAllowed(correo)) return "Correo no permitido"
        if (pass.isBlank() || confirmar.isBlank()) return "Ingresa y confirma la contraseña"
        if (pass != confirmar) return "Las contraseñas no coinciden"
        if (region.isBlank()) return "Selecciona una región"
        if (comuna.isBlank()) return "Selecciona una comuna"
        return null
    }

    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Registro") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).fillMaxWidth()
        ) {
            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre / Razón comercial") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(correo, { correo = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(confirmar, { confirmar = it }, label = { Text("Confirmar contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(telefono, { telefono = it }, label = { Text("Teléfono (opcional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))

            // --- DROPDOWN REGIÓN ---
            ExposedDropdownMenuBox(
                expanded = showRegiones,
                onExpandedChange = { showRegiones = !showRegiones }
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Región") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRegiones) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                // AQUÍ AGREGAMOS EL MODIFIER.HEIGHTIN
                ExposedDropdownMenu(
                    expanded = showRegiones,
                    onDismissRequest = { showRegiones = false },
                    modifier = Modifier.heightIn(max = 250.dp) // <--- ESTO ACTIVA EL SCROLL
                ) {
                    listaRegiones.forEach { reg ->
                        DropdownMenuItem(
                            text = { Text(reg) },
                            onClick = {
                                region = reg
                                comuna = ""
                                showRegiones = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            // --- DROPDOWN COMUNA ---
            ExposedDropdownMenuBox(
                expanded = showComunas,
                onExpandedChange = {
                    if (region.isNotBlank()) showComunas = !showComunas
                }
            ) {
                OutlinedTextField(
                    value = comuna,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Comuna") },
                    placeholder = { Text("Selecciona región primero") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showComunas) },
                    enabled = region.isNotBlank(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                // AQUÍ AGREGAMOS EL MODIFIER.HEIGHTIN TAMBIÉN
                ExposedDropdownMenu(
                    expanded = showComunas,
                    onDismissRequest = { showComunas = false },
                    modifier = Modifier.heightIn(max = 250.dp) // <--- ESTO ACTIVA EL SCROLL
                ) {
                    listaComunas.forEach { com ->
                        DropdownMenuItem(
                            text = { Text(com) },
                            onClick = {
                                comuna = com
                                showComunas = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = !loading,
                onClick = {
                    error = validar()
                    if (error != null) return@Button
                    loading = true
                    scope.launch {
                        val r = UserRepository.register(
                            nombre = nombre,
                            correo = correo,
                            contrasena = pass,
                            telefono = telefono.ifBlank { null },
                            region = region,
                            comuna = comuna
                        )
                        loading = false
                        if (r.isSuccess) {
                            ok = "Usuario registrado. Ahora inicia sesión."
                            onRegistroOk()
                        } else {
                            error = r.exceptionOrNull()?.message ?: "No se pudo registrar"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (loading) "Creando..." else "Crear cuenta") }

            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onBackLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            ok?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        }
    }
}