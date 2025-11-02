package com.example.tienda_react.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tienda_react.data.FakeData
import com.example.tienda_react.domain.Product
import com.example.tienda_react.ui.components.ProductThumb
import com.example.tienda_react.ui.theme.TiendaTheme
import com.example.tienda_react.viewmodel.CartViewModel

@Composable
fun DetalleScreen(
    id: Int,
    onGoCarrito: () -> Unit,
    cartVm: CartViewModel
) {
    val p = remember(id) { FakeData.byId(id) }
    DetalleContent(
        product = p,
        onAdd = { repeat(it) { cartVm.add(p) } },
        onGoCarrito = onGoCarrito
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetalleContent(
    product: Product,
    onAdd: (qty: Int) -> Unit,
    onGoCarrito: () -> Unit
) {
    var qty by remember { mutableStateOf(1) }
    val images = remember(product.id) { product.images.ifEmpty { listOf<String>() } }
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

            Text("Precio: $${product.price}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { if (qty > 1) qty-- }) { Text("-") }
                Text("$qty", style = MaterialTheme.typography.titleMedium)
                OutlinedButton(onClick = { qty++ }) { Text("+") }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onAdd(qty) }) { Text("Añadir al carrito") }
                OutlinedButton(onClick = onGoCarrito) { Text("Ir al carrito") }
            }
        }
    }
}

/* ---------------- PREVIEWS ---------------- */

private fun sampleProduct(): Product = Product(
    id = 99,
    name = "Skate Demo Pack",
    price = 59_990,
    category = "Patinetas",
    stock = 10,
    images = listOf(
        "file:///android_asset/IMG/Patinetas/skate1a.jpg",
        "file:///android_asset/IMG/Patinetas/skate2a.jpg",
        "file:///android_asset/IMG/Patinetas/skate3a.jpg"
    )
)

@Preview(name = "Detalle – Pixel 7", showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun Detalle_Preview() {
    TiendaTheme {
        DetalleContent(
            product = sampleProduct(),
            onAdd = { },
            onGoCarrito = { }
        )
    }
}

@Preview(name = "Detalle – Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Detalle_Dark_Preview() {
    TiendaTheme {
        DetalleContent(
            product = sampleProduct(),
            onAdd = { },
            onGoCarrito = { }
        )
    }
}
