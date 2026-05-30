package com.example.foodrecipe.repository

import com.example.foodrecipe.BuildConfig
import com.example.foodrecipe.data.api.RetrofitClient
import com.example.foodrecipe.data.model.RecipeDetail
import com.example.foodrecipe.data.model.RecipeSummary
import com.example.foodrecipe.data.prefs.UserPreferences
import kotlinx.coroutines.flow.first

class RecipeRepository(private val userPreferences: UserPreferences? = null) {

    suspend fun searchRecipes(query: String, type: String? = null): Result<List<RecipeSummary>> {
        return try {
            val diet = userPreferences?.dietFlow?.first()?.takeIf { it.isNotBlank() }
            val intolerances = userPreferences?.intolerancesFlow?.first()
                ?.takeIf { it.isNotEmpty() }
                ?.joinToString(",")
            val response = RetrofitClient.api.searchRecipes(
                query = query,
                apiKey = BuildConfig.SPOONACULAR_API_KEY,
                type = type,
                diet = diet,
                intolerances = intolerances
            )
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e.toRecipeError())
        }
    }

    suspend fun getRecipeDetail(id: Int): Result<RecipeDetail> {
        return try {
            val response = RetrofitClient.api.getRecipeDetail(
                id = id,
                apiKey = BuildConfig.SPOONACULAR_API_KEY
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e.toRecipeError())
        }
    }
}
