// utils/Text.kt
package com.example.tienda_react.utils

fun String.ellipsize(max: Int): String =
    if (length <= max) this else take(max - 1) + "…"
