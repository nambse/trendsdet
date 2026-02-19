package com.example.trend_sdet.fake

import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.domain.model.CartItem
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Money
import com.example.trend_sdet.domain.model.PriceRange
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductImage
import com.example.trend_sdet.domain.model.ProductVariant
import com.example.trend_sdet.domain.model.SelectedOption

object FakeData {

    val product1 = Product(
        id = "product-1",
        title = "Classic Sneakers",
        description = "Comfortable everyday sneakers for any occasion.",
        images = listOf(ProductImage("https://example.com/sneakers.jpg", "Sneakers")),
        priceRange = PriceRange(
            minPrice = Money("89.99", "USD"),
            maxPrice = Money("89.99", "USD"),
        ),
        variants = listOf(
            ProductVariant(
                id = "variant-1",
                title = "M / Black",
                price = Money("89.99", "USD"),
                compareAtPrice = null,
                availableForSale = true,
                selectedOptions = listOf(
                    SelectedOption("Size", "M"),
                    SelectedOption("Color", "Black"),
                ),
            ),
            ProductVariant(
                id = "variant-1b",
                title = "L / Black",
                price = Money("89.99", "USD"),
                compareAtPrice = null,
                availableForSale = true,
                selectedOptions = listOf(
                    SelectedOption("Size", "L"),
                    SelectedOption("Color", "Black"),
                ),
            ),
        ),
    )

    val product2 = Product(
        id = "product-2",
        title = "Running Shoes",
        description = "Lightweight running shoes for athletes.",
        images = listOf(ProductImage("https://example.com/running.jpg", "Running Shoes")),
        priceRange = PriceRange(
            minPrice = Money("129.99", "USD"),
            maxPrice = Money("129.99", "USD"),
        ),
        variants = listOf(
            ProductVariant(
                id = "variant-2",
                title = "L / White",
                price = Money("129.99", "USD"),
                compareAtPrice = Money("149.99", "USD"),
                availableForSale = true,
                selectedOptions = listOf(
                    SelectedOption("Size", "L"),
                    SelectedOption("Color", "White"),
                ),
            ),
        ),
    )

    val products = listOf(product1, product2)

    val collection1 = Collection(
        id = "col-1",
        title = "New Arrivals",
        description = "Latest products",
        imageUrl = "https://example.com/new.jpg",
    )

    val collection2 = Collection(
        id = "col-2",
        title = "Best Sellers",
        description = "Most popular items",
        imageUrl = "https://example.com/best.jpg",
    )

    val collections = listOf(collection1, collection2)

    val cartItem1 = CartItem(
        id = "line-1",
        quantity = 1,
        variantId = "variant-1",
        productId = "product-1",
        productTitle = "Classic Sneakers",
        variantTitle = "M / Black",
        price = Money("89.99", "USD"),
        imageUrl = "https://example.com/sneakers.jpg",
    )

    val cart = Cart(
        id = "cart-1",
        checkoutUrl = "https://shop.example.com/checkout",
        lines = listOf(cartItem1),
        totalAmount = Money("89.99", "USD"),
        subtotalAmount = Money("89.99", "USD"),
        totalQuantity = 1,
    )
}
