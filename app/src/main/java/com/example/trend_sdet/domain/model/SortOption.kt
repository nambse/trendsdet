package com.example.trend_sdet.domain.model

enum class SortOption(val displayLabel: String) {
    RELEVANCE("Relevance"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    NEWEST("Newest"),
    TITLE_AZ("Title: A-Z"),
}
