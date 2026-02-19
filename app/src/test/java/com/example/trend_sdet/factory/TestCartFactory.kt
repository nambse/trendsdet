package com.example.trend_sdet.factory

import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.domain.model.CartItem

object TestCartFactory {

    fun cartItem(
        id: String = "line-1",
        quantity: Int = 1,
        variantId: String = "variant-1",
        productId: String = "product-1",
        productTitle: String = "Test Product",
        variantTitle: String = "M / Black",
        price: TestProductFactory.() -> Unit = {},
        imageUrl: String? = "https://example.com/img.jpg",
    ) = CartItem(
        id = id,
        quantity = quantity,
        variantId = variantId,
        productId = productId,
        productTitle = productTitle,
        variantTitle = variantTitle,
        price = TestProductFactory.money(),
        imageUrl = imageUrl,
    )

    fun cart(
        id: String = "cart-1",
        checkoutUrl: String = "https://shop.example.com/checkout",
        lines: List<CartItem> = listOf(cartItem()),
        totalAmount: TestProductFactory.() -> Unit = {},
        subtotalAmount: TestProductFactory.() -> Unit = {},
        totalQuantity: Int = 1,
    ) = Cart(
        id = id,
        checkoutUrl = checkoutUrl,
        lines = lines,
        totalAmount = TestProductFactory.money("29.99"),
        subtotalAmount = TestProductFactory.money("29.99"),
        totalQuantity = totalQuantity,
    )

    fun emptyCart() = Cart(
        id = "cart-empty",
        checkoutUrl = "",
        lines = emptyList(),
        totalAmount = TestProductFactory.money("0.00"),
        subtotalAmount = TestProductFactory.money("0.00"),
        totalQuantity = 0,
    )
}
