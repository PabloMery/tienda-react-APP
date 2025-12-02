package com.example.tienda_react.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tienda_react.domain.Product
import com.example.tienda_react.ui.components.ProductThumb
import com.example.tienda_react.utils.asCLP
import com.example.tienda_react.viewmodel.CartViewModel
import com.example.tienda_react.viewmodel.ProductsViewModel

@Composable
fun DetalleScreen(
    id: Int,
    onGoCarrito: () -> Unit,
    cartVm: CartViewModel,
    // Usamos el mismo ViewModel para no recargar si ya tenemos la lista
    productsVm: ProductsViewModel = viewModel()
) {
    // Estado para guardar el producto cargado
    var product by remember { mutableStateOf<Product?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        // Pedimos al VM que busque el producto (en cache o API)
        product = productsVm.getProductDetail(id.toLong())
        loading = false
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val p = product
        if (p != null) {
            DetalleContent(
                product = p,
                onAdd = { qty -> repeat(qty) { cartVm.add(p) } },
                onGoCarrito = onGoCarrito
            )
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Producto no encontrado")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalleContent(
    product: Product,
    onAdd: (qty: Int) -> Unit,
    onGoCarrito: () -> Unit
) {
    var qty by remember { mutableStateOf(1) }
    // Usamos imageUrls corregidas
    val images = remember(product.id) { product.imageUrls.ifEmpty { listOf<String>() } }
    var selected by remember(product.id) { mutableStateOf(0) }

    Scaffold(topBar = { TopAppBar(title = { Text(product.name) }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {

            // Imagen principal
            val mainList = if (images.isNotEmpty()) listOf(images[selected]) else emptyList()
            ProductThumb(
                urls = mainList,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(10.dp))

            // Miniaturas
            if (images.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    itemsIndexed(images) { index, url ->
                        val isSelected = index == selected
                        ProductThumb(
                            urls = listOf(url),
                            modifier = Modifier
                                .size(72.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = MaterialTheme.shapes.small
                                )
                                .alpha(if (isSelected) 1f else 0.9f)
                                .clickable { selected = index },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Text("Precio: ${product.price.asCLP()}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text("Categoría: ${product.category}", style = MaterialTheme.typography.bodyMedium)
            Text("Stock disponible: ${product.stock}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = { if (qty > 1) qty-- }) { Text("-") }
                Text("$qty", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { if (qty < product.stock) qty++ }) { Text("+") }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onAdd(qty) },
                    enabled = product.stock > 0
                ) {
                    Text(if (product.stock > 0) "Añadir al carrito" else "Agotado")
                }
                OutlinedButton(onClick = onGoCarrito) { Text("Ir al carrito") }
            }
        }
    }
}