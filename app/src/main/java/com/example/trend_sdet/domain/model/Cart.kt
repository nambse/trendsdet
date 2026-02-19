package com.example.trend_sdet.domain.model

data class Cart(
    val id: String,
    val checkoutUrl: String,
    val lines: List<CartItem>,
    val totalAmount: Money,
    val subtotalAmount: Money,
    val totalQuantity: Int,
)
