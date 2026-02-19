package com.example.trend_sdet.data.repository

import com.example.trend_sdet.data.mapper.toDomain
import com.example.trend_sdet.domain.model.Cart
import com.example.trend_sdet.util.await
import com.shopify.buy3.GraphClient
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.ID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val graphClient: GraphClient,
) : CartRepository {

    private var cartId: String? = null

    private val _cartItemCount = MutableStateFlow(0)
    override val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    override suspend fun createCart(variantId: String, quantity: Int): Result<Cart> = runCatching {
        val mutation = Storefront.mutation { mutation ->
            mutation.cartCreate(
                { args ->
                    args.input(
                        Storefront.CartInput().setLines(
                            listOf(
                                Storefront.CartLineInput(ID(variantId)).setQuantity(quantity)
                            )
                        )
                    )
                },
            ) { payload ->
                payload
                    .cart { cart -> cartQueryFragment(cart) }
                    .userErrors { errors -> errors.field().message() }
            }
        }
        val response = graphClient.mutateGraph(mutation).await()
        val cartCreate = response.data!!.cartCreate
        val errors = cartCreate.userErrors
        if (errors.isNotEmpty()) {
            throw Exception(errors.joinToString { it.message })
        }
        val cart = cartCreate.cart.toDomain()
        cartId = cart.id
        _cartItemCount.value = cart.totalQuantity
        cart
    }

    override suspend fun getCart(): Result<Cart?> = runCatching {
        val id = cartId ?: return@runCatching null
        val query = Storefront.query { root ->
            root.cart(ID(id)) { cart -> cartQueryFragment(cart) }
        }
        val response = graphClient.queryGraph(query).await()
        response.data!!.cart?.toDomain()
    }

    override suspend fun addToCart(variantId: String, quantity: Int): Result<Cart> {
        val id = cartId ?: return createCart(variantId, quantity)
        return runCatching {
            val mutation = Storefront.mutation { mutation ->
                mutation.cartLinesAdd(
                    ID(id),
                    listOf(
                        Storefront.CartLineInput(ID(variantId)).setQuantity(quantity)
                    ),
                ) { payload ->
                    payload
                        .cart { cart -> cartQueryFragment(cart) }
                        .userErrors { errors -> errors.field().message() }
                }
            }
            val response = graphClient.mutateGraph(mutation).await()
            val cartLinesAdd = response.data!!.cartLinesAdd
            val errors = cartLinesAdd.userErrors
            if (errors.isNotEmpty()) {
                throw Exception(errors.joinToString { it.message })
            }
            val cart = cartLinesAdd.cart.toDomain()
            cartId = cart.id
            _cartItemCount.value = cart.totalQuantity
            cart
        }
    }

    override suspend fun updateCartLine(lineId: String, quantity: Int): Result<Cart> = runCatching {
        val id = cartId ?: throw IllegalStateException("No active cart")
        val mutation = Storefront.mutation { mutation ->
            mutation.cartLinesUpdate(
                ID(id),
                listOf(
                    Storefront.CartLineUpdateInput(ID(lineId)).setQuantity(quantity)
                ),
            ) { payload ->
                payload
                    .cart { cart -> cartQueryFragment(cart) }
                    .userErrors { errors -> errors.field().message() }
            }
        }
        val response = graphClient.mutateGraph(mutation).await()
        val cartLinesUpdate = response.data!!.cartLinesUpdate
        val errors = cartLinesUpdate.userErrors
        if (errors.isNotEmpty()) {
            throw Exception(errors.joinToString { it.message })
        }
        val cart = cartLinesUpdate.cart.toDomain()
        _cartItemCount.value = cart.totalQuantity
        cart
    }

    override suspend fun removeCartLine(lineId: String): Result<Cart> = runCatching {
        val id = cartId ?: throw IllegalStateException("No active cart")
        val mutation = Storefront.mutation { mutation ->
            mutation.cartLinesRemove(
                ID(id),
                listOf(ID(lineId)),
            ) { payload ->
                payload
                    .cart { cart -> cartQueryFragment(cart) }
                    .userErrors { errors -> errors.field().message() }
            }
        }
        val response = graphClient.mutateGraph(mutation).await()
        val cartLinesRemove = response.data!!.cartLinesRemove
        val errors = cartLinesRemove.userErrors
        if (errors.isNotEmpty()) {
            throw Exception(errors.joinToString { it.message })
        }
        val cart = cartLinesRemove.cart.toDomain()
        _cartItemCount.value = cart.totalQuantity
        cart
    }

    override fun clearLocalCart() {
        cartId = null
        _cartItemCount.value = 0
    }

    private fun cartQueryFragment(cart: Storefront.CartQuery): Storefront.CartQuery =
        cart
            .checkoutUrl()
            .totalQuantity()
            .cost { cost ->
                cost
                    .totalAmount { money -> money.amount().currencyCode() }
                    .subtotalAmount { money -> money.amount().currencyCode() }
            }
            .lines({ args -> args.first(50) }) { connection ->
                connection.edges { edge ->
                    edge.node { line ->
                        line
                            .id()
                            .quantity()
                            .cost { lineCost ->
                                lineCost.amountPerQuantity { money ->
                                    money.amount().currencyCode()
                                }
                            }
                            .merchandise { merchandise ->
                                merchandise.onProductVariant { variant ->
                                    variant
                                        .title()
                                        .price { money -> money.amount().currencyCode() }
                                        .image { image -> image.url() }
                                        .product { product -> product.title() }
                                }
                            }
                    }
                }
            }
}
