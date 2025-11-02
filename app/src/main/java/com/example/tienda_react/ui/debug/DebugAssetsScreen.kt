package com.example.tienda_react.ui.debug

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "DebugAssets"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugAssetsScreen(
    baseDir: String = "IMG" // cambia a "IMG/Monopatines" para filtrar subcarpeta
) {
    val ctx = LocalContext.current
    var files by remember { mutableStateOf<List<String>>(emptyList()) }
    var errors by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(baseDir) {
        val ok = mutableListOf<String>()
        val bad = mutableListOf<String>()

        // Enumeramos recursivamente IMG/ (un nivel)
        val dirs = withContext(Dispatchers.IO) { ctx.assets.list(baseDir)?.toList().orEmpty() }
        for (entry in dirs) {
            val path = "$baseDir/$entry"
            val isDir = withContext(Dispatchers.IO) { ctx.assets.list(path)?.isNotEmpty() == true }
            if (isDir) {
                val inner = withContext(Dispatchers.IO) { ctx.assets.list(path)?.toList().orEmpty() }
                for (f in inner) {
                    val full = "$path/$f"
                    // Probar open() para confirmar que existe en APK
                    try {
                        withContext(Dispatchers.IO) {
                            ctx.assets.open(full).use { /* ok */ }
                        }
                        ok += full
                    } catch (t: Throwable) {
                        bad += full
                        Log.w(TAG, "NO existe en assets: $full", t)
                    }
                }
            } else {
                // Es archivo directo dentro de IMG/
                try {
                    withContext(Dispatchers.IO) {
                        ctx.assets.open(path).use { /* ok */ }
                    }
                    ok += path
                } catch (t: Throwable) {
                    bad += path
                    Log.w(TAG, "NO existe en assets: $path", t)
                }
            }
        }
        files = ok.sorted()
        errors = bad.sorted()

        Log.i(TAG, "OK assets (${files.size}): ${files.joinToString()}")
        if (errors.isNotEmpty()) Log.w(TAG, "MISSING (${errors.size}): ${errors.joinToString()}")
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Debug Assets: $baseDir") }) }) { pad ->
        Column(Modifier.padding(pad).padding(12.dp)) {
            if (errors.isNotEmpty()) {
                Text("Faltantes en APK:", color = MaterialTheme.colorScheme.error)
                errors.forEach { Text("• $it", color = MaterialTheme.colorScheme.error) }
                Spacer(Modifier.height(12.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(files) { full ->
                    // probamos AMBOS esquemas:
                    val assetScheme = "asset:///$full"
                    val fileScheme  = "file:///android_asset/$full"

                    Text(full, style = MaterialTheme.typography.titleSmall)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("asset:///", style = MaterialTheme.typography.labelSmall)
                            AsyncImage(
                                model = assetScheme,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text("file:///android_asset/", style = MaterialTheme.typography.labelSmall)
                            AsyncImage(
                                model = fileScheme,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Divider()
                }
            }
        }
    }
}
