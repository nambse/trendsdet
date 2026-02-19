package com.example.trend_sdet.di

import com.example.trend_sdet.data.repository.CartRepository
import com.example.trend_sdet.data.repository.FavoriteRepository
import com.example.trend_sdet.data.repository.ProductRepository
import com.example.trend_sdet.fake.FakeCartRepository
import com.example.trend_sdet.fake.FakeFavoriteRepository
import com.example.trend_sdet.fake.FakeProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class],
)
object FakeRepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository = FakeProductRepository()

    @Provides
    @Singleton
    fun provideCartRepository(): CartRepository = FakeCartRepository()

    @Provides
    @Singleton
    fun provideFavoriteRepository(): FavoriteRepository = FakeFavoriteRepository()
}

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkModule::class],
)
object FakeNetworkModule

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object FakeDatabaseModule
