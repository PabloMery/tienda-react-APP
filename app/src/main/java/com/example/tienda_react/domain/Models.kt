package com.example.tienda_react.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Helper para que las imágenes del localhost se vean en el emulador Android
fun fixImageUrl(url: String): String {
    if (url.startsWith("http") || url.startsWith("file://")) return url
    // IP especial del emulador para acceder a tu PC
    val baseUrl = "http://10.0.2.2:8080"
    val cleanUrl = if (url.startsWith("/")) url else "/$url"
    return "$baseUrl$cleanUrl"
}

// Producto: ID Long (compatible con Java) y lista de imágenes nullable
data class Product(
    val id: Long? = null,
    val name: String,
    val price: Int,
    val category: String,
    val stock: Int,
    val images: List<String>? = null
) : Serializable {
    val imageUrls: List<String>
        get() = images?.map { fixImageUrl(it) } ?: emptyList()
}

// Carrito (UI): El modelo que usa tu pantalla de Android
data class CartItem(
    val id: Long,
    val product: Product,
    val qty: Int
)

// Usuario
data class User(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    @SerializedName("pass")
    val contrasena: String,
    val telefono: String? = null,
    val region: String,
    val comuna: String
) : Serializable

// --- DTOs para comunicación con el Backend ---

// Input: Lo que envías al agregar items
data class CartItemRequest(val productId: Long, val quantity: Int)

// NUEVO: Lo que RECIBES realmente del backend (solo IDs y cantidades)
data class CartItemBackend(
    val productId: Long,
    val quantity: Int
)

// NUEVO: La estructura que envuelve la lista en la respuesta del backend
data class CartResponseBackend(
    val items: List<CartItemBackend> = emptyList(),
    val id: Long? = null,
    val userId: Long? = null
)