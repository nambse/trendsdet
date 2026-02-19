package com.example.trend_sdet.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Cart?>>(UiState.Loading)
    val uiState: StateFlow<UiState<Cart?>> = _uiState.asStateFlow()

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            if (_uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            cartRepository.getCart()
                .onSuccess { cart ->
                    _uiState.value = UiState.Success(cart)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load cart")
                }
        }
    }

    fun updateQuantity(lineId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartLine(lineId, quantity)
                .onSuccess { cart ->
                    _uiState.value = UiState.Success(cart)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to update quantity")
                }
        }
    }

    fun removeItem(lineId: String) {
        viewModelScope.launch {
            cartRepository.removeCartLine(lineId)
                .onSuccess { cart ->
                    _uiState.value = UiState.Success(cart)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to remove item")
                }
        }
    }
}
