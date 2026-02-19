package com.example.trend_sdet.data.repository

import com.example.trend_sdet.data.mapper.toDomain
import com.example.trend_sdet.data.mapper.toShopifyParams
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption
import com.example.trend_sdet.util.await
import com.shopify.buy3.GraphClient
import com.shopify.buy3.Storefront
import com.shopify.graphql.support.ID
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val graphClient: GraphClient,
) : ProductRepository {

    override suspend fun getProducts(first: Int): Result<List<Product>> = runCatching {
        val query = Storefront.query { root ->
            root.products({ args -> args.first(first) }) { connection ->
                connection.edges { edge ->
                    edge.node { product ->
                        productQueryFragment(product)
                    }
                }
            }
        }
        val response = graphClient.queryGraph(query).await()
        response.data!!.products.edges.map { it.node.toDomain() }
    }

    override suspend fun getProductById(id: String): Result<Product> = runCatching {
        val query = Storefront.query { root ->
            root.node(ID(id)) { node ->
                node.onProduct { product ->
                    productQueryFragment(product)
                }
            }
        }
        val response = graphClient.queryGraph(query).await()
        (response.data!!.node as Storefront.Product).toDomain()
    }

    override suspend fun searchProducts(
        query: String,
        first: Int,
        sortKey: SortOption,
    ): Result<List<Product>> =
        runCatching {
            val sortParams = sortKey.toShopifyParams()
            val searchQuery = Storefront.query { root ->
                root.products({ args ->
                    args.first(first)
                        .query(query)
                        .sortKey(sortParams.sortKey)
                        .reverse(sortParams.reverse)
                }) { connection ->
                    connection.edges { edge ->
                        edge.node { product ->
                            productQueryFragment(product)
                        }
                    }
                }
            }
            val response = graphClient.queryGraph(searchQuery).await()
            response.data!!.products.edges.map { it.node.toDomain() }
        }

    override suspend fun getCollections(first: Int): Result<List<Collection>> = runCatching {
        val query = Storefront.query { root ->
            root.collections({ args -> args.first(first) }) { connection ->
                connection.edges { edge ->
                    edge.node { collection ->
                        collection
                            .title()
                            .description()
                            .image { image -> image.url() }
                    }
                }
            }
        }
        val response = graphClient.queryGraph(query).await()
        response.data!!.collections.edges.map { it.node.toDomain() }
    }

    private fun productQueryFragment(product: Storefront.ProductQuery): Storefront.ProductQuery =
        product
            .title()
            .description()
            .images({ args -> args.first(10) }) { connection ->
                connection.edges { edge ->
                    edge.node { image ->
                        image.url().altText()
                    }
                }
            }
            .priceRange { priceRange ->
                priceRange
                    .minVariantPrice { money -> money.amount().currencyCode() }
                    .maxVariantPrice { money -> money.amount().currencyCode() }
            }
            .variants({ args -> args.first(10) }) { connection ->
                connection.edges { edge ->
                    edge.node { variant ->
                        variant
                            .title()
                            .price { money -> money.amount().currencyCode() }
                            .compareAtPriceV2 { money -> money.amount().currencyCode() }
                            .availableForSale()
                            .selectedOptions { option -> option.name().value() }
                    }
                }
            }
}
