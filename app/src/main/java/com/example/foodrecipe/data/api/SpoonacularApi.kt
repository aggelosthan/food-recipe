package com.example.foodrecipe.data.api

import com.example.foodrecipe.data.model.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApi {
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String
    ): RecipeSearchResponse
}
