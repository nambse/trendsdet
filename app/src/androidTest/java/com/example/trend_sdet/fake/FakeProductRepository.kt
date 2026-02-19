package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption

class FakeProductRepository : ProductRepository {

    override suspend fun getProducts(first: Int): Result<List<Product>> =
        Result.success(FakeData.products)

    override suspend fun getProductById(id: String): Result<Product> {
        val product = FakeData.products.find { it.id == id }
        return if (product != null) {
            Result.success(product)
        } else {
            Result.failure(Exception("Product not found"))
        }
    }

    override suspend fun searchProducts(
        query: String,
        first: Int,
        sortKey: SortOption,
    ): Result<List<Product>> {
        val filtered = FakeData.products.filter {
            it.title.contains(query, ignoreCase = true)
        }
        return Result.success(filtered)
    }

    override suspend fun getCollections(first: Int): Result<List<Collection>> =
        Result.success(FakeData.collections)
}
