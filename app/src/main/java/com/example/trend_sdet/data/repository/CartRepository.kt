package com.example.trend_sdet.data.repository

import com.example.trend_sdet.domain.model.Cart
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartItemCount: StateFlow<Int>
    suspend fun createCart(variantId: String, quantity: Int = 1): Result<Cart>
    suspend fun getCart(): Result<Cart?>
    suspend fun addToCart(variantId: String, quantity: Int = 1): Result<Cart>
    suspend fun updateCartLine(lineId: String, quantity: Int): Result<Cart>
    suspend fun removeCartLine(lineId: String): Result<Cart>
    fun clearLocalCart()
}
