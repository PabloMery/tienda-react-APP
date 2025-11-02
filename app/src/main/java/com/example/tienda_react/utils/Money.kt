// utils/Money.kt
package com.example.tienda_react.utils

import java.text.NumberFormat
import java.util.Locale

val clLocale = Locale("es", "CL")
val moneyCLP: NumberFormat = NumberFormat.getCurrencyInstance(clLocale).apply {
    maximumFractionDigits = 0 // CLP sin decimales
}

fun Int.asCLP(): String = moneyCLP.format(this)
fun Long.asCLP(): String = moneyCLP.format(this)
fun Double.asCLP(): String = moneyCLP.format(this)
