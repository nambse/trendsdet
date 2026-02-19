package com.example.trend_sdet.ui.screens.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.domain.model.Cart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CheckoutUiState {
    data class Summary(val cart: Cart) : CheckoutUiState
    object Processing : CheckoutUiState
    data class Success(val orderNumber: String, val totalFormatted: String) : CheckoutUiState
    data class Error(val message: String) : CheckoutUiState
    object Loading : CheckoutUiState
}

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Loading)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadCartSummary()
    }

    private fun loadCartSummary() {
        viewModelScope.launch {
            cartRepository.getCart()
                .onSuccess { cart ->
                    if (cart != null && cart.lines.isNotEmpty()) {
                        _uiState.value = CheckoutUiState.Summary(cart)
                    } else {
                        _uiState.value = CheckoutUiState.Error("Cart is empty")
                    }
                }
                .onFailure { error ->
                    _uiState.value = CheckoutUiState.Error(
                        error.message ?: "Failed to load cart"
                    )
                }
        }
    }

    fun placeOrder() {
        val current = _uiState.value
        if (current !is CheckoutUiState.Summary) return

        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Processing
            // Simulate order processing
            delay(2000)
            val orderNumber = "TRD-${(100000..999999).random()}"
            _uiState.value = CheckoutUiState.Success(
                orderNumber = orderNumber,
                totalFormatted = current.cart.totalAmount.formatted,
            )
            cartRepository.clearLocalCart()
        }
    }
}
