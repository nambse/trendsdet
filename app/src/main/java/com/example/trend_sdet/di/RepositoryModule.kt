package com.example.trend_sdet.di

import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.data.repository.CartRepositoryImpl
import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.data.repository.FavoriteRepositoryImpl
import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.data.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}
