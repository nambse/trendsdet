package com.example.trend_sdet.domain.model

data class ProductVariant(
    val id: String,
    val title: String,
    val price: Money,
    val compareAtPrice: Money?,
    val availableForSale: Boolean,
    val selectedOptions: List<SelectedOption>,
)

data class SelectedOption(
    val name: String,
    val value: String,
)
