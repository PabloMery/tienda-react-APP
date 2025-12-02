package com.example.tienda_react.viewmodel

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

    // Estado para feedback de operaciones de admin (ej: "Producto creado!")
    private val _adminMessage = MutableStateFlow<String?>(null)
    val adminMessage: StateFlow<String?> = _adminMessage

    private var currentProduct: Product? = null

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductsUiState.Loading
            val result = ProductRepository.getAllProducts()
            result.fold(
                onSuccess = { list ->
                    _uiState.value = ProductsUiState.Success(list)
                },
                onFailure = { ex ->
                    _uiState.value = ProductsUiState.Error(ex.message ?: "Error desconocido")
                }
            )
        }
    }

    suspend fun getProductDetail(id: Long): Product? {
        val state = _uiState.value
        if (state is ProductsUiState.Success) {
            val found = state.products.find { it.id == id }
            if (found != null) return found
        }
        val apiResult = ProductRepository.getProductById(id)
        return apiResult.getOrNull()
    }

    // --- ACCIONES DE ADMIN ---

    fun createProduct(name: String, price: Int, category: String, stock: Int, imageUrl: String) {
        viewModelScope.launch {
            val newProduct = Product(
                id = 0, // El backend asignará el ID real
                name = name,
                price = price,
                category = category,
                stock = stock,
                images = if (imageUrl.isNotBlank()) listOf(imageUrl) else emptyList()
            )
            val result = ProductRepository.createProduct(newProduct)
            if (result.isSuccess) {
                _adminMessage.value = "Producto creado con éxito"
                loadProducts() // Recargar la lista
            } else {
                _adminMessage.value = "Error: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            val result = ProductRepository.deleteProduct(id)
            if (result.isSuccess) {
                _adminMessage.value = "Producto eliminado"
                loadProducts() // Recargar la lista para quitar el borrado
            } else {
                _adminMessage.value = "Error al eliminar: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearAdminMessage() {
        _adminMessage.value = null
    }
}