package com.example.trend_sdet.data.repository

import com.example.trend_sdet.data.local.FavoriteDao
import com.example.trend_sdet.data.local.FavoriteEntity
import com.example.trend_sdet.data.mapper.toProduct
import com.example.trend_sdet.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<Product>> =
        favoriteDao.getAllFavorites().map { entities ->
            entities.map { it.toProduct() }
        }

    override fun getAllFavoriteIds(): Flow<Set<String>> =
        favoriteDao.getAllFavoriteIds().map { it.toSet() }

    override fun isFavorite(productId: String): Flow<Boolean> =
        favoriteDao.isFavorite(productId)

    override suspend fun toggleFavorite(product: Product) {
        val isFav = favoriteDao.isFavorite(product.id).first()
        if (isFav) {
            favoriteDao.removeFavorite(product.id)
        } else {
            favoriteDao.addFavorite(
                FavoriteEntity(
                    productId = product.id,
                    title = product.title,
                    imageUrl = product.images.firstOrNull()?.url,
                    minPrice = product.priceRange.minPrice.amount,
                    maxPrice = product.priceRange.maxPrice.amount,
                    currencyCode = product.priceRange.minPrice.currencyCode,
                )
            )
        }
    }

    override suspend fun removeFavorite(productId: String) {
        favoriteDao.removeFavorite(productId)
    }
}
