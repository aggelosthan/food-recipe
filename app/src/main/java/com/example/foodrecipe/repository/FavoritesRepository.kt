package com.example.foodrecipe.repository

import com.example.foodrecipe.data.db.FavoriteRecipeDao
import com.example.foodrecipe.data.db.FavoriteRecipeEntity
import com.example.foodrecipe.data.model.RecipeDetail
import kotlinx.coroutines.flow.Flow

class FavoritesRepository(private val dao: FavoriteRecipeDao) {

    suspend fun addFavorite(detail: RecipeDetail) {
        dao.insert(
            FavoriteRecipeEntity(
                id = detail.id,
                title = detail.title,
                image = detail.image ?: ""
            )
        )
    }

    suspend fun removeFavorite(id: Int) = dao.delete(id)

    suspend fun isFavorite(id: Int): Boolean = dao.isFavorite(id)

    fun observeAll(): Flow<List<FavoriteRecipeEntity>> = dao.observeAll()
}
