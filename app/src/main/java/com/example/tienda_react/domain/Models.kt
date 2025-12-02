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
    val id: Long? = null, // Null al crear, Long al leer
    val name: String,
    val price: Int,
    val category: String,
    val stock: Int,
    val images: List<String>? = null
) : Serializable {
    // Si no hay imágenes, devolvemos lista vacía para no romper la UI
    val imageUrls: List<String>
        get() = images?.map { fixImageUrl(it) } ?: emptyList()
}

// Carrito: Actualizado para usar Long en el ID
data class CartItem(
    val id: Long,
    val product: Product,
    val qty: Int
)

// Usuario: Mapeo correcto de 'pass'
data class User(
    val id: Long? = null,
    val nombre: String,
    val correo: String,
    @SerializedName("pass") // CLAVE: El backend espera "pass", no "contrasena"
    val contrasena: String,
    val telefono: String? = null,
    val region: String,
    val comuna: String
) : Serializable

// DTOs auxiliares para el microservicio de carrito (si lo usas a futuro)
data class CartItemRequest(val productId: Long, val quantity: Int)
data class CartResponse(val items: List<CartItem> = emptyList(), val totalPrice: Int = 0)