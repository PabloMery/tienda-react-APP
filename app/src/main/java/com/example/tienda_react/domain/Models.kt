package com.example.tienda_react.domain

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// --- CLASES DE PRODUCTOS (Se mantienen igual) ---
@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Int,
    val category: String,
    val stock: Int,
    val images: List<String> = emptyList()
)

@Serializable
data class CartItem(val id: Int, val product: Product, val qty: Int)

// --- CLASE USER CORREGIDA ---
@Serializable
data class User(
    // CORRECCIÓN: Cambiado a Long? (nullable) e inicializado en null
    // Esto hará que al convertirlo a JSON, el campo "id" no se envíe o viaje como null
    val id: Long? = null,

    val nombre: String,
    val correo: String,

    // Mapeo para que Android use "contrasena" pero la API reciba "pass"
    @SerializedName("pass")
    val contrasena: String,

    val telefono: String? = null,
    val region: String,
    val comuna: String
)
@Serializable
data class CartItemRequest(
    val productId: Long,
    val quantity: Int
)

@Serializable
data class CartResponse(
    val id: Long,
    val userId: Long,
    val status: String,
    val items: List<CartItemResponse>
)

@Serializable
data class CartItemResponse(
    val productId: Long,
    val quantity: Int
)