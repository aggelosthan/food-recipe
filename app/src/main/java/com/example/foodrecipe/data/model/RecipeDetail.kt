package com.example.foodrecipe.data.model

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    val instructions: String?,
    val extendedIngredients: List<Ingredient>,
    val nutrition: Nutrition?,
    val analyzedInstructions: List<AnalyzedInstruction>
)

data class Ingredient(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String
)

data class Nutrition(
    val nutrients: List<Nutrient>
)

data class Nutrient(
    val name: String,
    val amount: Double,
    val unit: String
)

data class AnalyzedInstruction(
    val name: String,
    val steps: List<InstructionStep>
)

data class InstructionStep(
    val number: Int,
    val step: String
)
