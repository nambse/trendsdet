package com.example.trend_sdet.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val imageUrl: String?,
    val minPrice: String,
    val maxPrice: String,
    val currencyCode: String,
    val addedAt: Long = System.currentTimeMillis(),
)
