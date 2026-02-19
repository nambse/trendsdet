package com.example.trend_sdet.fake

import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteRepository : FavoriteRepository {

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())

    var toggleFavoriteCallCount = 0
        private set
    var lastToggledProduct: Product? = null
        private set
    var removeFavoriteCallCount = 0
        private set

    fun setFavorites(products: List<Product>) {
        _favorites.value = products
    }

    override fun getAllFavorites(): Flow<List<Product>> = _favorites

    override fun getAllFavoriteIds(): Flow<Set<String>> =
        _favorites.map { list -> list.map { it.id }.toSet() }

    override fun isFavorite(productId: String): Flow<Boolean> =
        _favorites.map { list -> list.any { it.id == productId } }

    override suspend fun toggleFavorite(product: Product) {
        toggleFavoriteCallCount++
        lastToggledProduct = product
        val current = _favorites.value.toMutableList()
        val existing = current.indexOfFirst { it.id == product.id }
        if (existing >= 0) {
            current.removeAt(existing)
        } else {
            current.add(product)
        }
        _favorites.value = current
    }

    override suspend fun removeFavorite(productId: String) {
        removeFavoriteCallCount++
        _favorites.value = _favorites.value.filter { it.id != productId }
    }
}
