package com.example.trend_sdet.di

import android.content.Context
import androidx.room.Room
import com.example.trend_sdet.data.local.FavoriteDao
import com.example.trend_sdet.data.local.TrendSdetDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TrendSdetDatabase =
        Room.databaseBuilder(
            context,
            TrendSdetDatabase::class.java,
            "trend_sdet_db",
        ).build()

    @Provides
    fun provideFavoriteDao(database: TrendSdetDatabase): FavoriteDao =
        database.favoriteDao()
}
