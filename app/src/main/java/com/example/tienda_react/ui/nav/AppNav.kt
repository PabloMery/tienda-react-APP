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
import com.example.tienda_react.viewmodel.ProductsViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

private sealed class Route(val route: String, val label: String, val icon: String) {
    data object Home       : Route("home",      "Inicio",    "🏠")
    data object Productos  : Route("productos", "Productos", "🛒")
    data object Carrito    : Route("carrito",   "Carrito",   "🧺")
    data object Admin      : Route("admin",     "Admin",     "🔧") // Pestaña Admin
    data object Login      : Route("login",     "Login",     "")
    data object Registro   : Route("registro",  "Registro",  "")
    data object Debug      : Route("debug",     "Debug",     "")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav() {
    val nav = rememberNavController()
    // ViewModels compartidos
    val cartVm: CartViewModel = viewModel()
    val productsVm: ProductsViewModel = viewModel()

    val backEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backEntry?.destination?.route?.substringBefore("/")
    val ui = cartVm.ui.collectAsState().value

    // Añadimos Admin a la barra inferior
    val items = listOf(Route.Home, Route.Productos, Route.Carrito, Route.Admin)

    val showBottomBar = currentRoute !in listOf(Route.Login.route, Route.Registro.route)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Tienda React") }) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (selected) return@NavigationBarItem
                                nav.navigate(item.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                if (item is Route.Carrito && ui.totalItems > 0) {
                                    BadgedBox(badge = { Badge { Text("${ui.totalItems}") } }) { Text(item.icon) }
                                } else { Text(item.icon) }
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = Route.Login.route, modifier = Modifier.padding(pad)) {
            // Auth
            composable(Route.Login.route) {
                LoginScreen(
                    onGoRegistro = { nav.navigate(Route.Registro.route) },
                    onLoginOk = { nav.navigate(Route.Home.route) { popUpTo(Route.Login.route) { inclusive = true } } }
                )
            }
            composable(Route.Registro.route) {
                RegistroScreen(
                    onBackLogin = { nav.popBackStack() },
                    onRegistroOk = { nav.popBackStack() }
                )
            }

            // Tabs
            composable(Route.Home.route) {
                HomeScreen(
                    onGo = { nav.navigate(Route.Productos.route) },
                    onDebug = { nav.navigate(Route.Debug.route) }
                )
            }
            composable(Route.Productos.route) {
                ProductosScreen(
                    onOpen = { id -> nav.navigate("detalle/$id") },
                    onGoCart = { nav.navigate(Route.Carrito.route) },
                    cartVm = cartVm,
                    productsVm = productsVm
                )
            }
            composable(Route.Carrito.route) {
                CarritoScreen(cartVm = cartVm)
            }
            composable(Route.Admin.route) {
                AdminScreen(productsVm = productsVm)
            }

            // Extras
            composable("detalle/{id}") { back ->
                val id = back.arguments?.getString("id")?.toIntOrNull() ?: 0
                DetalleScreen(id, { nav.navigate(Route.Carrito.route) }, cartVm, productsVm)
            }
            composable(Route.Debug.route) {
                com.example.tienda_react.ui.debug.DebugAssetsScreen("IMG")
            }
        }
    }
}