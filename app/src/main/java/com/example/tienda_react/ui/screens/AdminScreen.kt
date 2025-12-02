package com.example.tienda_react.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tienda_react.ui.components.ProductThumb
import com.example.tienda_react.utils.asCLP
import com.example.tienda_react.viewmodel.ProductsUiState
import com.example.tienda_react.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    productsVm: ProductsViewModel = viewModel()
) {
    val state by productsVm.uiState.collectAsState()
    val adminMsg by productsVm.adminMessage.collectAsState()
    val context = LocalContext.current

    // Mostrar Toasts con feedback
    LaunchedEffect(adminMsg) {
        adminMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            productsVm.clearAdminMessage()
        }
    }

    // Estado del formulario de creación (Dialog)
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Administración") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear")
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (val s = state) {
                is ProductsUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ProductsUiState.Error -> {
                    Button(
                        onClick = { productsVm.loadProducts() },
                        modifier = Modifier.align(Alignment.Center)
                    ) { Text("Reintentar: ${s.message}") }
                }
                is ProductsUiState.Success -> {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.products) { p ->
                            Card {
                                Row(
                                    Modifier.padding(8.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    ProductThumb(urls = p.imageUrls, modifier = Modifier.size(60.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                                        Text("${p.price.asCLP()} - Stock: ${p.stock}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    IconButton(onClick = { productsVm.deleteProduct(p.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateProductDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, price, cat, stock, img ->
                productsVm.createProduct(name, price, cat, stock, img)
                showDialog = false
            }
        )
    }
}

@Composable
fun CreateProductDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imgUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { if (it.all { c -> c.isDigit() }) priceStr = it },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") })
                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { if (it.all { c -> c.isDigit() }) stockStr = it },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(value = imgUrl, onValueChange = { imgUrl = it }, label = { Text("URL Imagen (opcional)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toIntOrNull() ?: 0
                    val s = stockStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && p > 0) {
                        onCreate(name, p, category, s, imgUrl)
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}