package com.example.tienda_react.domain


import kotlinx.serialization.Serializable

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

@Serializable
data class User(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val telefono: String? = null,
    val region: String,
    val comuna: String
)