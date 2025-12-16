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
            ProductRepository.getAllProducts().fold(
                onSuccess = { _uiState.value = ProductsUiState.Success(it) },
                onFailure = { _uiState.value = ProductsUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun createProduct(context: Context, name: String, price: Int, category: String, stock: Int, imageUri: Uri?) {
        viewModelScope.launch {
            _adminMessage.value = "Subiendo imagen..."
            var imgUrl = ""

            // 1. Si hay imagen, la subimos primero
            if (imageUri != null) {
                val upload = ProductRepository.uploadImage(context, imageUri)
                if (upload.isSuccess) imgUrl = upload.getOrDefault("")
                else {
                    _adminMessage.value = "Error subiendo imagen"
                    return@launch
                }
            }

            // 2. Creamos el producto con la URL obtenida
            _adminMessage.value = "Guardando producto..."
            val newProduct = Product(
                name = name,
                price = price,
                category = category,
                stock = stock,
                images = if(imgUrl.isNotEmpty()) listOf(imgUrl) else null
            )

            val result = ProductRepository.createProduct(newProduct)
            if (result.isSuccess) {
                _adminMessage.value = "¡Producto Creado!"
                loadProducts() // Recargar lista automáticamente
            } else {
                _adminMessage.value = "Error al crear: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun deleteProduct(id: Long?) {
        if (id == null) return
        viewModelScope.launch {
            if (ProductRepository.deleteProduct(id).isSuccess) {
                _adminMessage.value = "Eliminado"
                loadProducts()
            } else {
                _adminMessage.value = "Error al eliminar"
            }
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

    fun clearAdminMessage() { _adminMessage.value = null }

}