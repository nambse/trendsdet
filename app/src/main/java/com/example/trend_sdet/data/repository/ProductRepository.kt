package com.example.trend_sdet.data.repository

import com.example.trend_sdet.domain.model.Collection
import com.example.trend_sdet.domain.model.Product
import com.example.trend_sdet.domain.model.SortOption

interface ProductRepository {
    suspend fun getProducts(first: Int = 20): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun searchProducts(query: String, first: Int = 20, sortKey: SortOption = SortOption.RELEVANCE): Result<List<Product>>
    suspend fun getCollections(first: Int = 10): Result<List<Collection>>
}
