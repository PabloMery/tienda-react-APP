package com.example.tienda_react.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tienda_react.ui.screens.*
import com.example.tienda_react.viewmodel.CartViewModel
import androidx.lifecycle.viewmodel.compose.viewModel



private sealed class Route(val route: String, val label: String, val icon: String) {
    // Tabs inferiores
    data object Home         : Route("home",        "Inicio",   "🏠")
    data object Productos    : Route("productos",   "Productos","🛒")
    data object Blog         : Route("blog",        "Blog",     "📰")
    data object QuienesSomos : Route("quienes",     "Nosotros", "👥")
    data object Carrito      : Route("carrito",     "Carrito",  "🧺")
    data object DebugAssets  : Route("debug-assets","Debug",    "🧪")

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val nav = rememberNavController()
    val cartVm: CartViewModel = viewModel()

    val backEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route?.substringBefore("/")
    val ui = cartVm.ui.collectAsState().value

    val items = listOf(
        Route.Home,
        Route.Productos,
        Route.Blog,
        Route.QuienesSomos,
        Route.Carrito
    )

    val showBottomBar = when (currentRoute) {
        Route.Login.route, Route.Registro.route -> false
        else -> true
    }

    val topTitle = when (currentRoute) {
        Route.Login.route        -> "Iniciar sesión"
        Route.Registro.route     -> "Registro"
        Route.Home.route         -> "TiendaReact"
        Route.Productos.route    -> "Productos"
        Route.Blog.route         -> "Blog"
        Route.QuienesSomos.route -> "Quiénes somos"
        Route.Carrito.route      -> "Carrito (${ui.totalItems})"
        Route.DebugAssets.route  -> "Debug assets"
        else                     -> "TiendaReact"
    }

    val showBottomBar = when (currentRoute) {
        Route.Login.route, Route.Registro.route -> false
        else -> true
    }

    val topTitle = when (currentRoute) {
        Route.Login.route       -> "Iniciar sesión"
        Route.Registro.route    -> "Registro"
        Route.Home.route        -> "TiendaReact"
        Route.Productos.route   -> "Productos"
        Route.Carrito.route     -> "Carrito (${ui.totalItems})"
        Route.DebugAssets.route -> "Debug assets"
        else                    -> "TiendaReact"
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        when (currentRoute) {
                            Route.Home.route      -> "TiendaReact"
                            Route.Productos.route -> "Productos"
                            Route.Carrito.route   -> "Carrito (${ui.totalItems})"
                            Route.DebugAssets.route-> "Debug assets"
                            else                  -> "TiendaReact"
                        }
                    )
                }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (selected) return@NavigationBarItem
                                val popped = nav.popBackStack(item.route, inclusive = false)
                                if (!popped) {
                                    nav.navigate(item.route) {
                                        popUpTo(nav.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                if (item is Route.Carrito && ui.totalItems > 0) {
                                    BadgedBox(badge = { Badge { Text("${ui.totalItems}") } }) {
                                        Text(item.icon)
                                    }
                                } else {
                                    Text(item.icon)
                                }
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.route,//Route.DebugAssets.route
            modifier = Modifier.padding(pad)
        ) {
            composable(Route.Home.route) {
                HomeScreen(
                    onGo = { nav.navigate(Route.Productos.route) },
                    onDebug = { nav.navigate(Route.DebugAssets.route) }
                )
            }

            composable(Route.Productos.route) {
                ProductosScreen(
                    onOpen = { id -> nav.navigate("detalle/$id") },
                    onGoCart = { nav.navigate(Route.Carrito.route) },
                    cartVm = cartVm

                )
            }
            composable("detalle/{id}") { back ->
                val id = back.arguments?.getString("id")?.toIntOrNull() ?: 0
                DetalleScreen(
                    id = id,
                    onGoCarrito = { nav.navigate(Route.Carrito.route) },
                    cartVm = cartVm
                )
            }

            composable(Route.Carrito.route) {
                CarritoScreen(cartVm = cartVm)
            }

            composable(Route.DebugAssets.route) {
                com.example.tienda_react.ui.debug.DebugAssetsScreen(baseDir = "IMG")
            }
        }
    }
}
