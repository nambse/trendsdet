package com.example.trend_sdet.domain.model

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val images: List<ProductImage>,
    val priceRange: PriceRange,
    val variants: List<ProductVariant>,
)

data class ProductImage(
    val url: String,
    val altText: String?,
)

data class PriceRange(
    val minPrice: Money,
    val maxPrice: Money,
)

data class Money(
    val amount: String,
    val currencyCode: String,
) {
    val formatted: String
        get() {
            val value = amount.toDoubleOrNull() ?: 0.0
            return when (currencyCode) {
                "USD" -> "${"$"}%.2f".format(value)
                "EUR" -> "%.2f\u20AC".format(value)
                "TRY" -> "%.2f\u20BA".format(value)
                "GBP" -> "\u00A3%.2f".format(value)
                else -> "%.2f %s".format(value, currencyCode)
            }
        }
}
