package com.example.trend_sdet.ui.screens.productdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductVariant
import com.example.trend_sdet.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OptionGroup(
    val name: String,
    val values: List<String>,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Product>>(UiState.Loading)
    val uiState: StateFlow<UiState<Product>> = _uiState.asStateFlow()

    private val _selectedVariant = MutableStateFlow<ProductVariant?>(null)
    val selectedVariant: StateFlow<ProductVariant?> = _selectedVariant.asStateFlow()

    private val _selectedOptions = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedOptions: StateFlow<Map<String, String>> = _selectedOptions.asStateFlow()

    private val _optionGroups = MutableStateFlow<List<OptionGroup>>(emptyList())
    val optionGroups: StateFlow<List<OptionGroup>> = _optionGroups.asStateFlow()

    private val _addToCartState = MutableStateFlow<AddToCartState>(AddToCartState.Idle)
    val addToCartState: StateFlow<AddToCartState> = _addToCartState.asStateFlow()

    private val _currentProductId = MutableStateFlow<String?>(null)

    val isFavorite: StateFlow<Boolean> = _currentProductId.flatMapLatest { id ->
        if (id != null) favoriteRepository.isFavorite(id) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun loadProduct(productId: String) {
        _currentProductId.value = productId
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _uiState.value = UiState.Success(product)
                    deriveOptionGroups(product)
                    val firstAvailable = product.variants.firstOrNull { it.availableForSale }
                        ?: product.variants.firstOrNull()
                    if (firstAvailable != null) {
                        _selectedVariant.value = firstAvailable
                        _selectedOptions.value = firstAvailable.selectedOptions
                            .associate { it.name to it.value }
                    }
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load product")
                }
        }
    }

    private fun deriveOptionGroups(product: Product) {
        val groupMap = linkedMapOf<String, MutableSet<String>>()
        product.variants.forEach { variant ->
            variant.selectedOptions.forEach { option ->
                groupMap.getOrPut(option.name) { linkedSetOf() }.add(option.value)
            }
        }
        _optionGroups.value = groupMap.map { (name, values) ->
            OptionGroup(name, values.toList())
        }
    }

    fun selectOption(groupName: String, value: String) {
        val product = (_uiState.value as? UiState.Success)?.data ?: return
        val newOptions = _selectedOptions.value.toMutableMap()
        newOptions[groupName] = value
        _selectedOptions.value = newOptions

        val matchingVariant = product.variants.find { variant ->
            variant.selectedOptions.all { option ->
                newOptions[option.name] == option.value
            }
        }
        if (matchingVariant != null) {
            _selectedVariant.value = matchingVariant
        }
    }

    fun selectVariant(variant: ProductVariant) {
        _selectedVariant.value = variant
        _selectedOptions.value = variant.selectedOptions.associate { it.name to it.value }
    }

    fun toggleFavorite() {
        val product = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(product)
        }
    }

    fun increaseQuantity() {
        _quantity.value = (_quantity.value + 1).coerceAtMost(10)
    }

    fun decreaseQuantity() {
        _quantity.value = (_quantity.value - 1).coerceAtLeast(1)
    }

    fun addToCart() {
        val variant = _selectedVariant.value ?: return
        val qty = _quantity.value
        viewModelScope.launch {
            _addToCartState.value = AddToCartState.Loading
            cartRepository.addToCart(variant.id, qty)
                .onSuccess {
                    _addToCartState.value = AddToCartState.Success
                    _quantity.value = 1
                }
                .onFailure { error ->
                    _addToCartState.value = AddToCartState.Error(
                        error.message ?: "Failed to add to cart"
                    )
                }
        }
    }

    fun resetAddToCartState() {
        _addToCartState.value = AddToCartState.Idle
    }
}

sealed interface AddToCartState {
    data object Idle : AddToCartState
    data object Loading : AddToCartState
    data object Success : AddToCartState
    data class Error(val message: String) : AddToCartState
}
