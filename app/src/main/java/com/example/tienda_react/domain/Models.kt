package com.example.tienda_react.domain

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Helper para arreglar URLs relativas del backend
fun fixImageUrl(url: String): String {
    if (url.startsWith("http") || url.startsWith("file://")) return url
    val baseUrl = "http://10.0.2.2:8080"
    val cleanUrl = if (url.startsWith("/")) url else "/$url"
    return "$baseUrl$cleanUrl"
}

data class Product(
    val id: Long? = null,
    val name: String,
    val price: Int,
    val category: String,
    val stock: Int,

    // CAMBIO CLAVE: Hacemos la lista nullable (List<String>?)
    // Gson le asignará 'null' si viene null del servidor.
    val images: List<String>? = null
) : Serializable {

    // Propiedad computada segura:
    // Si 'images' es null, devolvemos una lista vacía para que la UI no se rompa.
    val imageUrls: List<String>
        get() = images?.map { fixImageUrl(it) } ?: emptyList()
}

data class CartItem(
    val id: Long,
    val product: Product,
    val qty: Int
)

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

data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

data class CartResponse(
    val items: List<CartItem> = emptyList(),
    val totalPrice: Int = 0
)