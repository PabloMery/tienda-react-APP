package com.example.tienda_react.utils
private val EMAIL_RX = Regex("^[^\\s@]+@(duoc\\.cl|profesor\\.duoc\\.cl|gmail\\.com)$", RegexOption.IGNORE_CASE)

fun isValidEmail(email: String) = EMAIL_RX.matches(email)
fun samePassword(p1: String, p2: String) = p1.isNotBlank() && p1 == p2
fun isNotBlankField(s: String) = s.trim().isNotEmpty()
