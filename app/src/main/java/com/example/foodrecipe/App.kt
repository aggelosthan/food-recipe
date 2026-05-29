package com.example.foodrecipe

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.example.foodrecipe.data.db.AppDatabase
import com.example.foodrecipe.data.prefs.UserPreferences
import com.example.foodrecipe.repository.FavoritesRepository

class App : Application(), SingletonImageLoader.Factory {

    val database by lazy { AppDatabase.getInstance(this) }
    val favoritesRepository by lazy { FavoritesRepository(database.favoriteRecipeDao()) }
    val userPreferences by lazy { UserPreferences(this) }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(OkHttpNetworkFetcherFactory()) }
            .build()
}
