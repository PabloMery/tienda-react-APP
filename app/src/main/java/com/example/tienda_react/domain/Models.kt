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
