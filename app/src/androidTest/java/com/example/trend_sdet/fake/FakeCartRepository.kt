package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.domain.model.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartRepository : CartRepository {

    private val _cartItemCount = MutableStateFlow(FakeData.cart.totalQuantity)
    override val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private var currentCart: Cart = FakeData.cart

    override suspend fun createCart(variantId: String, quantity: Int): Result<Cart> =
        Result.success(currentCart)

    override suspend fun getCart(): Result<Cart?> =
        Result.success(currentCart)

    override suspend fun addToCart(variantId: String, quantity: Int): Result<Cart> {
        _cartItemCount.value += quantity
        return Result.success(currentCart)
    }

    override suspend fun updateCartLine(lineId: String, quantity: Int): Result<Cart> =
        Result.success(currentCart)

    override suspend fun removeCartLine(lineId: String): Result<Cart> {
        val updated = currentCart.copy(
            lines = currentCart.lines.filter { it.id != lineId },
            totalQuantity = (currentCart.totalQuantity - 1).coerceAtLeast(0),
        )
        currentCart = updated
        _cartItemCount.value = updated.totalQuantity
        return Result.success(updated)
    }

    override fun clearLocalCart() {
        _cartItemCount.value = 0
    }
}
