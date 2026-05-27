package com.example.foodrecipe.data.model

data class RecipeSearchResponse(
    val results: List<RecipeSummary>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)
