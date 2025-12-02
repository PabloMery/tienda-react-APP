package com.example.tienda_react.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tienda_react.domain.Product
import com.example.tienda_react.ui.components.ProductThumb
import com.example.tienda_react.utils.asCLP
import com.example.tienda_react.viewmodel.CartViewModel
import com.example.tienda_react.viewmodel.ProductsUiState
import com.example.tienda_react.viewmodel.ProductsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductosScreen(
    onOpen: (Int) -> Unit,
    onGoCart: () -> Unit,
    cartVm: CartViewModel,
    productsVm: ProductsViewModel = viewModel()
) {
    val state by productsVm.uiState.collectAsState()
    val cartUi = cartVm.ui.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                actions = {
                    TextButton(onClick = onGoCart) {
                        Text("🛒 ")
                        AnimatedContent(
                            targetState = cartUi.totalItems,
                            // CORRECCIÓN: 'with' -> 'togetherWith'
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "badge"
                        ) { count ->
                            Text("$count", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize()) {
            when (val s = state) {
                is ProductsUiState.Loading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                is ProductsUiState.Error -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error de conexión", color = MaterialTheme.colorScheme.error)
                        Text(s.message, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { productsVm.loadProducts() }) { Text("Reintentar") }
                    }
                }
                is ProductsUiState.Success -> {
                    if (s.products.isEmpty()) {
                        Text("No hay productos disponibles.", Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(s.products) { p ->
                                ProductCard(p, onOpen, onAdd = { cartVm.add(it) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(p: Product, onOpen: (Int) -> Unit, onAdd: (Product) -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "scale")

    Card(
        modifier = Modifier.fillMaxWidth().scale(scale)
            .clickable {
                pressed = true
                onOpen(p.id?.toInt() ?: 0)
                pressed = false
            }
    ) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProductThumb(urls = p.imageUrls, modifier = Modifier.size(100.dp))
            Column(Modifier.weight(1f)) {
                Text(p.name, style = MaterialTheme.typography.titleMedium)
                Text(p.price.asCLP())
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onAdd(p) }) { Text("Añadir al carrito") }
            }
        }
    }
}