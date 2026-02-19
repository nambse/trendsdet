package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoriteRepository : FavoriteRepository {

    private val favorites = MutableStateFlow<List<Product>>(emptyList())

    override fun getAllFavorites(): Flow<List<Product>> = favorites

    override fun getAllFavoriteIds(): Flow<Set<String>> =
        favorites.map { list -> list.map { it.id }.toSet() }

    override fun isFavorite(productId: String): Flow<Boolean> =
        favorites.map { list -> list.any { it.id == productId } }

    override suspend fun toggleFavorite(product: Product) {
        favorites.update { current ->
            if (current.any { it.id == product.id }) {
                current.filter { it.id != product.id }
            } else {
                current + product
            }
        }
    }

    override suspend fun removeFavorite(productId: String) {
        favorites.update { current ->
            current.filter { it.id != productId }
        }
    }
}
