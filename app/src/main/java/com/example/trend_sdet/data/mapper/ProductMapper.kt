package com.example.trend_sdet.data.mapper

import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Money
import com.example.trend_sdet.domain.model.PriceRange
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductImage
import com.example.trend_sdet.domain.model.ProductVariant
import com.example.trend_sdet.domain.model.SelectedOption
import com.shopify.buy3.Storefront

fun Storefront.Product.toDomain(): Product = Product(
    id = id.toString(),
    title = title,
    description = description,
    images = images.edges.map { it.node.toDomain() },
    priceRange = priceRange.toDomain(),
    variants = variants.edges.map { it.node.toDomain() },
)

fun Storefront.Image.toDomain(): ProductImage = ProductImage(
    url = url,
    altText = altText,
)

fun Storefront.ProductPriceRange.toDomain(): PriceRange = PriceRange(
    minPrice = minVariantPrice.toDomain(),
    maxPrice = maxVariantPrice.toDomain(),
)

fun Storefront.MoneyV2.toDomain(): Money = Money(
    amount = amount.toString(),
    currencyCode = currencyCode.name,
)

fun Storefront.ProductVariant.toDomain(): ProductVariant = ProductVariant(
    id = id.toString(),
    title = title,
    price = price.toDomain(),
    compareAtPrice = compareAtPriceV2?.toDomain(),
    availableForSale = availableForSale,
    selectedOptions = selectedOptions.map { it.toDomain() },
)

fun Storefront.SelectedOption.toDomain(): SelectedOption = SelectedOption(
    name = name,
    value = value,
)

fun Storefront.Collection.toDomain(): Collection = Collection(
    id = id.toString(),
    title = title,
    description = description ?: "",
    imageUrl = image?.url,
)
