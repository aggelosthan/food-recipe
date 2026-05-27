package com.example.foodrecipe.repository

import com.example.foodrecipe.BuildConfig
import com.example.foodrecipe.data.api.RetrofitClient
import com.example.foodrecipe.data.model.RecipeSummary

class RecipeRepository {

    suspend fun searchRecipes(query: String): Result<List<RecipeSummary>> {
        return try {
            val response = RetrofitClient.api.searchRecipes(
                query = query,
                apiKey = BuildConfig.SPOONACULAR_API_KEY
            )
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
