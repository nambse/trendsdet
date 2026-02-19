package com.example.trend_sdet.di

import android.content.Context
import com.example.trend_sdet.BuildConfig
import com.shopify.buy3.GraphClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGraphClient(@ApplicationContext context: Context): GraphClient =
        GraphClient.build(
            context = context,
            shopDomain = BuildConfig.SHOPIFY_DOMAIN,
            accessToken = BuildConfig.SHOPIFY_STOREFRONT_TOKEN,
        )
}
