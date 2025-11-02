package com.example.tienda_react.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent

/**
 * Muestra la PRIMERA imagen disponible de una lista (assets).
 * - Acepta rutas tipo: file:///android_asset/IMG/Monopatines/mono2a.jpg
 * - Muestra loader mientras carga y contenido “cover” al terminar.
 */
@Composable
fun ProductThumb(
    urls: List<String>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val model = urls.firstOrNull()

    // Subcompose para manejar estados (loading/error) bonito
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    ) {
        when (painter.state) {
            is AsyncImagePainter.State.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AsyncImagePainter.State.Error -> {
                // Si quieres, aquí puedes poner un placeholder local (drawable)
                // AsyncImage(model = R.drawable.placeholder, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = contentScale)
                Box(Modifier.fillMaxSize()) { /* vacío o placeholder */ }
            }
            else -> SubcomposeAsyncImageContent() // pinta la imagen cargada
        }
    }
}
