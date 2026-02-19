package com.example.trend_sdet.data.mapper

import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.domain.model.CartItem
import com.shopify.buy3.Storefront

fun Storefront.Cart.toDomain(): Cart = Cart(
    id = id.toString(),
    checkoutUrl = checkoutUrl,
    lines = lines.edges.map { it.node.toDomain() },
    totalAmount = cost.totalAmount.toDomain(),
    subtotalAmount = cost.subtotalAmount.toDomain(),
    totalQuantity = totalQuantity,
)

fun Storefront.BaseCartLine.toDomain(): CartItem {
    val variant = merchandise as? Storefront.ProductVariant
    return CartItem(
        id = id.toString(),
        quantity = quantity,
        variantId = variant?.id?.toString() ?: "",
        productId = variant?.product?.id?.toString() ?: "",
        productTitle = variant?.product?.title ?: "",
        variantTitle = variant?.title ?: "",
        price = cost.amountPerQuantity.toDomain(),
        imageUrl = variant?.image?.url,
    )
}
