package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.domain.model.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartRepository : CartRepository {

    private val _cartItemCount = MutableStateFlow(0)
    override val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    var getCartResult: Result<Cart?> = Result.success(null)
    var addToCartResult: Result<Cart> = Result.failure(Exception("Not set"))
    var updateCartLineResult: Result<Cart> = Result.failure(Exception("Not set"))
    var removeCartLineResult: Result<Cart> = Result.failure(Exception("Not set"))
    var createCartResult: Result<Cart> = Result.failure(Exception("Not set"))

    var clearLocalCartCalled = false
        private set
    var lastAddToCartVariantId: String? = null
        private set
    var lastAddToCartQuantity: Int? = null
        private set
    var lastUpdateLineId: String? = null
        private set
    var lastUpdateQuantity: Int? = null
        private set
    var lastRemoveLineId: String? = null
        private set

    override suspend fun createCart(variantId: String, quantity: Int): Result<Cart> {
        return createCartResult
    }

    override suspend fun getCart(): Result<Cart?> {
        return getCartResult
    }

    override suspend fun addToCart(variantId: String, quantity: Int): Result<Cart> {
        lastAddToCartVariantId = variantId
        lastAddToCartQuantity = quantity
        return addToCartResult
    }

    override suspend fun updateCartLine(lineId: String, quantity: Int): Result<Cart> {
        lastUpdateLineId = lineId
        lastUpdateQuantity = quantity
        return updateCartLineResult
    }

    override suspend fun removeCartLine(lineId: String): Result<Cart> {
        lastRemoveLineId = lineId
        return removeCartLineResult
    }

    override fun clearLocalCart() {
        clearLocalCartCalled = true
        _cartItemCount.value = 0
    }
}
