package com.example.trend_sdet.data.mapper

import com.example.trend_sdet.data.local.FavoriteEntity
import com.example.trend_sdet.domain.model.Money
import com.example.trend_sdet.domain.model.PriceRange
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductImage

fun FavoriteEntity.toProduct(): Product = Product(
    id = productId,
    title = title,
    description = "",
    images = if (imageUrl != null) listOf(ProductImage(url = imageUrl, altText = null)) else emptyList(),
    priceRange = PriceRange(
        minPrice = Money(amount = minPrice, currencyCode = currencyCode),
        maxPrice = Money(amount = maxPrice, currencyCode = currencyCode),
    ),
    variants = emptyList(),
)
