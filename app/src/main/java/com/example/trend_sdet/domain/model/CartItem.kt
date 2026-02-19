package com.example.trend_sdet.domain.model

data class CartItem(
    val id: String,
    val quantity: Int,
    val variantId: String,
    val productId: String,
    val productTitle: String,
    val variantTitle: String,
    val price: Money,
    val imageUrl: String?,
)
