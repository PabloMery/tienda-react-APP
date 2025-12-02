package com.example.tienda_react.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
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

    LaunchedEffect(adminMsg) {
        adminMsg?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            productsVm.clearAdminMessage()
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Panel Administrador") }) },
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
                                    // Usamos las URLs arregladas del modelo
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
            onCreate = { name, price, cat, stock, uri ->
                productsVm.createProduct(context, name, price, cat, stock, uri)
                showDialog = false
            }
        )
    }
}

@Composable
fun CreateProductDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Int, String, Int, Uri?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var stockStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para abrir la galería
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Producto") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth() // Asegura ancho para la imagen
            ) {
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

                Spacer(Modifier.height(8.dp))

                // Botón para seleccionar imagen
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedUri != null) "Cambiar Imagen" else "Seleccionar Imagen")
                }

                // Previsualización de la imagen seleccionada
                if (selectedUri != null) {
                    AsyncImage(
                        model = selectedUri,
                        contentDescription = "Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toIntOrNull() ?: 0
                    val s = stockStr.toIntOrNull() ?: 0
                    if (name.isNotBlank() && p > 0) {
                        onCreate(name, p, category, s, selectedUri)
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}