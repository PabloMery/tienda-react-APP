package com.example.tienda_react.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tienda_react.data.products.ProductRepository
import com.example.tienda_react.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProductsUiState {
    data object Loading : ProductsUiState()
    data class Success(val products: List<Product>) : ProductsUiState()
    data class Error(val message: String) : ProductsUiState()
}

class ProductsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState

    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            val result = ProductRepository.getAllProducts()
            result.fold(
                onSuccess = { list -> _uiState.value = ProductsUiState.Success(list) },
                onFailure = { ex -> _uiState.value = ProductsUiState.Error(ex.message ?: "Error desconocido") }
            )
        }
    }

    suspend fun getProductDetail(id: Long): Product? {
        val state = _uiState.value
        if (state is ProductsUiState.Success) {
            val found = state.products.find { it.id == id }
            if (found != null) return found
        }
        return ProductRepository.getProductById(id).getOrNull()
    }

    // --- ADMIN: Crear con soporte de Imagen ---

    fun createProduct(
        context: Context,
        name: String,
        price: Int,
        category: String,
        stock: Int,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _adminMessage.value = "Procesando..."

            var finalImageUrl = ""

            // 1. Si hay imagen seleccionada, subirla primero
            if (imageUri != null) {
                val uploadResult = ProductRepository.uploadImage(context, imageUri)
                if (uploadResult.isSuccess) {
                    finalImageUrl = uploadResult.getOrDefault("")
                } else {
                    _adminMessage.value = "Error subiendo imagen: ${uploadResult.exceptionOrNull()?.message}"
                    return@launch
                }
            }

            // 2. Crear el producto con la URL obtenida (o vacía)
            val newProduct = Product(
                name = name,
                price = price,
                category = category,
                stock = stock,
                images = if (finalImageUrl.isNotBlank()) listOf(finalImageUrl) else emptyList()
            )

            val result = ProductRepository.createProduct(newProduct)
            if (result.isSuccess) {
                _adminMessage.value = "Producto creado con éxito"
                loadProducts()
            } else {
                _adminMessage.value = "Error al crear producto: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun deleteProduct(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            val result = ProductRepository.deleteProduct(id)
            if (result.isSuccess) {
                _adminMessage.value = "Producto eliminado"
                loadProducts()
            } else {
                _adminMessage.value = "Error al eliminar: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearAdminMessage() {
        _adminMessage.value = null
    }
}