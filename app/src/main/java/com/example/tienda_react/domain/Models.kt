package com.example.tienda_react.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Helper para arreglar URLs
fun fixImageUrl(url: String): String {
    if (url.startsWith("http") || url.startsWith("file://")) return url
    val baseUrl = "http://10.0.2.2:8080"
    val cleanUrl = if (url.startsWith("/")) url else "/$url"
    return "$baseUrl$cleanUrl"
}

@Serializable
data class Product(
    val id: Long,
    val name: String,
    val price: Int,
    val category: String,
    val stock: Int,
    val images: List<String> = emptyList()
) {
    val imageUrls: List<String>
        get() = images.map { fixImageUrl(it) }
}

@Serializable
data class CartItem(
    val id: Long,
    val product: Product,
    val qty: Int
)

@Serializable
data class User(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    @SerialName("pass")
    val contrasena: String,
    val telefono: String? = null,
    val region: String,
    val comuna: String
)

// --- NUEVAS CLASES PARA EL MICROSERVICIO DE CARRITO (Puerto 8082) ---

// Lo que enviamos para agregar/actualizar ítems
@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

// Lo que responde el servidor del carrito
@Serializable
data class CartResponse(
    val items: List<CartItem> = emptyList(), // Asume que el microservice devuelve la estructura completa
    val totalPrice: Int = 0
)