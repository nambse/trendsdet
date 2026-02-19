package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption

class FakeProductRepository : ProductRepository {

    var productsResult: Result<List<Product>> = Result.success(emptyList())
    var productByIdResult: Result<Product> = Result.failure(Exception("Not set"))
    var searchResult: Result<List<Product>> = Result.success(emptyList())
    var collectionsResult: Result<List<Collection>> = Result.success(emptyList())

    var getProductsCallCount = 0
        private set
    var getCollectionsCallCount = 0
        private set
    var lastSearchQuery: String? = null
        private set
    var lastSearchSortKey: SortOption? = null
        private set

    override suspend fun getProducts(first: Int): Result<List<Product>> {
        getProductsCallCount++
        return productsResult
    }

    override suspend fun getProductById(id: String): Result<Product> {
        return productByIdResult
    }

    override suspend fun searchProducts(
        query: String,
        first: Int,
        sortKey: SortOption,
    ): Result<List<Product>> {
        lastSearchQuery = query
        lastSearchSortKey = sortKey
        return searchResult
    }

    override suspend fun getCollections(first: Int): Result<List<Collection>> {
        getCollectionsCallCount++
        return collectionsResult
    }
}
