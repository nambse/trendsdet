package com.example.trend_sdet.data.mapper

import com.example.trend_sdet.domain.model.SortOption
import com.shopify.buy3.Storefront

data class ShopifySortParams(
    val sortKey: Storefront.ProductSortKeys,
    val reverse: Boolean,
)

fun SortOption.toShopifyParams(): ShopifySortParams = when (this) {
    SortOption.RELEVANCE -> ShopifySortParams(Storefront.ProductSortKeys.RELEVANCE, false)
    SortOption.PRICE_LOW_TO_HIGH -> ShopifySortParams(Storefront.ProductSortKeys.PRICE, false)
    SortOption.PRICE_HIGH_TO_LOW -> ShopifySortParams(Storefront.ProductSortKeys.PRICE, true)
    SortOption.NEWEST -> ShopifySortParams(Storefront.ProductSortKeys.CREATED_AT, true)
    SortOption.TITLE_AZ -> ShopifySortParams(Storefront.ProductSortKeys.TITLE, false)
}
