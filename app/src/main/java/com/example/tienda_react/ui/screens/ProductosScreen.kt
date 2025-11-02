package com.example.tienda_react.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tienda_react.data.FakeData
import com.example.tienda_react.ui.components.ProductThumb
import com.example.tienda_react.ui.theme.TiendaTheme
import com.example.tienda_react.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductosScreen(
    onOpen: (Int) -> Unit,
    onGoCart: () -> Unit,
    cartVm: CartViewModel
) {
    val ui = cartVm.ui.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                actions = {
                    TextButton(onClick = onGoCart) {
                        Text("🛒 ")
                        AnimatedContent(
                            targetState = ui.totalItems,
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
        LazyColumn(
            Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(FakeData.PRODUCTS) { p ->
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
                            onOpen(p.id)
                            pressed = false
                        }
                ) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProductThumb(
                            urls = p.images,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${p.price}")
                            Spacer(Modifier.height(8.dp))

                            var bump by remember { mutableStateOf(false) }
                            val bumpScale by animateFloatAsState(
                                targetValue = if (bump) 1.1f else 1f,
                                label = "bump"
                            )
                            val scope = rememberCoroutineScope()

                            Button(
                                onClick = {
                                    cartVm.add(p)
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
}

/* ---------------- PREVIEWS (sin VM) ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductosScreenStatic() {
    Scaffold(topBar = { TopAppBar(title = { Text("Productos") }) }) { pad ->
        LazyColumn(
            Modifier.padding(pad).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(FakeData.PRODUCTS) { p ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ProductThumb(
                            urls = p.images,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            Text("$${p.price}")
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { }) { Text("Añadir al carrito") }
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Productos – Lista", showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun Productos_List_Preview() {
    TiendaTheme { ProductosScreenStatic() }
}

@Preview(name = "Productos – Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun Productos_List_Dark_Preview() {
    TiendaTheme { ProductosScreenStatic() }
}

@Preview(name = "Productos – Texto grande", showBackground = true, fontScale = 1.2f)
@Composable
fun Productos_List_Font_Preview() {
    TiendaTheme { ProductosScreenStatic() }
}
