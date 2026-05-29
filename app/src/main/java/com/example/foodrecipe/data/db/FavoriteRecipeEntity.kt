package com.example.foodrecipe.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val image: String,
    val savedAt: Long = System.currentTimeMillis()
)
