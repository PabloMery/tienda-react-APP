package com.example.tienda_react.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.tienda_react.ui.theme.TiendaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onGo: () -> Unit,onDebug: () -> Unit) {

    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { show = true }

    Scaffold(topBar = { TopAppBar(title = { Text("Tangana") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            AnimatedVisibility(visible = show, enter = fadeIn(tween(600)), exit = fadeOut()) {
                Column {
                    Text("Bienvenido(a)", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(12.dp))
                    Text("Explora productos y arma tu carrito.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onGo) { Text("Ver productos") }
                }
            }
        }
    }
}
@Preview(showBackground = true, name = "Home Preview")
@Composable
fun HomeScreenPreview() {
    TiendaTheme {
        HomeScreen(
            onGo = {},
            onDebug = TODO()
        )

    }
}
