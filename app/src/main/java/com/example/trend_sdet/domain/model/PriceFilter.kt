package com.example.trend_sdet.domain.model

data class PriceFilter(
    val minPrice: Double = 0.0,
    val maxPrice: Double = Double.MAX_VALUE,
)
