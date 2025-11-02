package com.example.tienda_react.data.cart

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tienda_react.domain.CartItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore("tiendareact_prefs")

class CartStore(private val context: Context) {
    private val KEY_CART = stringPreferencesKey("cart_json")
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    suspend fun read(): List<CartItem> {
        val str = context.dataStore.data.map { it[KEY_CART] ?: "[]" }.first()
        return runCatching { json.decodeFromString<List<CartItem>>(str) }.getOrElse { emptyList() }
    }

    suspend fun write(items: List<CartItem>) {
        val str = json.encodeToString(items)
        context.dataStore.edit { it[KEY_CART] = str }
    }
}
