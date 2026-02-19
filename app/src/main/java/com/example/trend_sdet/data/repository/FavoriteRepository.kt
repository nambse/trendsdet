package com.example.trend_sdet.data.repository

import com.example.trend_sdet.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<Product>>
    fun getAllFavoriteIds(): Flow<Set<String>>
    fun isFavorite(productId: String): Flow<Boolean>
    suspend fun toggleFavorite(product: Product)
    suspend fun removeFavorite(productId: String)
}
