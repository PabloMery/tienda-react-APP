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
import com.example.tienda_react.viewmodel.ProductsViewModel
import com.example.tienda_react.viewmodel.ProductsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductosScreen(
    onOpen: (Int) -> Unit, // Ojo: tu navegación usa Int, aunque el ID sea Long. Haremos cast.
    onGoCart: () -> Unit,
    cartVm: CartViewModel,
    // Inyectamos el ViewModel de productos
    productsVm: ProductsViewModel = viewModel()
) {
    val cartUi = cartVm.ui.collectAsState().value
    val state by productsVm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                actions = {
                    TextButton(onClick = onGoCart) {
                        Text("🛒 ")
                        AnimatedContent(
                            targetState = cartUi.totalItems,
                            transitionSpec = { fadeIn() with fadeOut() },
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
                        Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { productsVm.loadProducts() }) { Text("Reintentar") }
                    }
                }
                is ProductsUiState.Success -> {
                    if (s.products.isEmpty()) {
                        Text("No hay productos disponibles.", Modifier.align(Alignment.Center))
                    } else {
                        ProductList(
                            products = s.products,
                            onOpen = onOpen,
                            onAddToCart = { cartVm.add(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onOpen: (Int) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyColumn(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { p ->
            var pressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (pressed) 0.96f else 1f,
                animationSpec = spring(),
                label = "card-scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale)
                    .clickable {
                        pressed = true
                        // CORRECCIÓN: Usamos el operador safe call (?.) y el elvis (?:)
                        // Si p.id es null, pasamos 0.
                        onOpen(p.id?.toInt() ?: 0)
                        pressed = false
                    }
            ) {
                Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Usamos imageUrls (la propiedad computada que arregla el link)
                    ProductThumb(
                        urls = p.imageUrls,
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(Modifier.weight(1f)) {
                        Text(p.name, style = MaterialTheme.typography.titleMedium)
                        Text(p.price.asCLP())
                        Spacer(Modifier.height(8.dp))

                        var bump by remember { mutableStateOf(false) }
                        val bumpScale by animateFloatAsState(
                            targetValue = if (bump) 1.1f else 1f,
                            label = "bump"
                        )
                        val scope = rememberCoroutineScope()

                        Button(
                            onClick = {
                                onAddToCart(p)
                                bump = true
                                scope.launch {
                                    delay(120)
                                    bump = false
                                }
                            },
                            modifier = Modifier.scale(bumpScale)
                        ) { Text("Añadir al carrito") }
                    }
                }
            }
        }
    }
}