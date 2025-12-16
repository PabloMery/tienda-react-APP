package com.example.tienda_react.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.tienda_react.viewmodel.ProductsViewModel

@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: ProductsViewModel = viewModel(),
    context: Context = LocalContext.current
) {
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") } // Nota: El endpoint actual a veces no pide descripción, pero lo dejamos por si acaso
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }

    // Estados para el menú desplegable (Categoría)
    var categoria by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Monopatines", "Patines", "Patinetas", "BMX", "Accesorios")

    // Estado de la imagen
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Selector de imágenes
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Observar mensajes del ViewModel (Éxito o Error)
    val adminMessage by viewModel.adminMessage.collectAsState()

    // Efecto para mostrar Toasts y navegar si es necesario
    LaunchedEffect(adminMessage) {
        adminMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            if (msg.contains("Creado", ignoreCase = true)) {
                // Limpiar campos o navegar atrás si se creó con éxito
                viewModel.clearAdminMessage()
                navController.popBackStack()
            } else {
                viewModel.clearAdminMessage()
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Agregar Producto",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        // Nota: Si tu API backend no recibe descripción en el JSON de createProduct, este campo es visual.
        // Si quieres enviarlo, asegúrate de actualizar el modelo Product y el ViewModel.
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // --- SELECTOR DE CATEGORÍA ---
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = { },
                readOnly = true,
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Seleccionar categoría"
                        )
                    }
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                categories.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(text = opcion) },
                        onClick = {
                            categoria = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar Imagen")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Vista previa",
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )
        }

        Button(
            onClick = {
                // Validación simple
                if (nombre.isBlank() || precio.isBlank() || categoria.isBlank() || stock.isBlank()) {
                    Toast.makeText(context, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Conversión segura de números
                val precioInt = precio.toIntOrNull() ?: 0
                val stockInt = stock.toIntOrNull() ?: 0

                // Llamada a la función CORRECTA del ViewModel (sin Multipart manual)
                viewModel.createProduct(
                    context = context,
                    name = nombre,
                    price = precioInt,
                    category = categoria,
                    stock = stockInt,
                    imageUri = imageUri
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Guardar Producto")
        }
    }
}