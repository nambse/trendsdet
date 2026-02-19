package com.example.trend_sdet.factory

import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Money
import com.example.trend_sdet.domain.model.PriceRange
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.ProductImage
import com.example.trend_sdet.domain.model.ProductVariant
import com.example.trend_sdet.domain.model.SelectedOption

object TestProductFactory {

    fun money(amount: String = "29.99", currency: String = "USD") =
        Money(amount = amount, currencyCode = currency)

    fun selectedOption(name: String = "Size", value: String = "M") =
        SelectedOption(name = name, value = value)

    fun variant(
        id: String = "variant-1",
        title: String = "M / Black",
        price: Money = money(),
        compareAtPrice: Money? = null,
        availableForSale: Boolean = true,
        selectedOptions: List<SelectedOption> = listOf(
            selectedOption("Size", "M"),
            selectedOption("Color", "Black"),
        ),
    ) = ProductVariant(
        id = id,
        title = title,
        price = price,
        compareAtPrice = compareAtPrice,
        availableForSale = availableForSale,
        selectedOptions = selectedOptions,
    )

    fun product(
        id: String = "product-1",
        title: String = "Test Product",
        description: String = "A test product description",
        images: List<ProductImage> = listOf(ProductImage("https://example.com/img.jpg", "Alt")),
        priceRange: PriceRange = PriceRange(money("29.99"), money("49.99")),
        variants: List<ProductVariant> = listOf(variant()),
    ) = Product(
        id = id,
        title = title,
        description = description,
        images = images,
        priceRange = priceRange,
        variants = variants,
    )

    fun collection(
        id: String = "collection-1",
        title: String = "Summer Collection",
        description: String = "Summer items",
        imageUrl: String? = "https://example.com/collection.jpg",
    ) = Collection(id = id, title = title, description = description, imageUrl = imageUrl)

    fun productWithVariants(): Product {
        val variants = listOf(
            variant("v-sm-black", "S / Black", money("29.99"), selectedOptions = listOf(
                selectedOption("Size", "S"), selectedOption("Color", "Black"),
            )),
            variant("v-md-black", "M / Black", money("29.99"), selectedOptions = listOf(
                selectedOption("Size", "M"), selectedOption("Color", "Black"),
            )),
            variant("v-lg-black", "L / Black", money("34.99"), selectedOptions = listOf(
                selectedOption("Size", "L"), selectedOption("Color", "Black"),
            )),
            variant("v-sm-white", "S / White", money("29.99"), selectedOptions = listOf(
                selectedOption("Size", "S"), selectedOption("Color", "White"),
            )),
            variant("v-md-white", "M / White", money("29.99"), availableForSale = false, selectedOptions = listOf(
                selectedOption("Size", "M"), selectedOption("Color", "White"),
            )),
        )
        return product(variants = variants)
    }
}
