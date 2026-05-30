package com.example.foodrecipe.repository

import android.content.Context
import retrofit2.HttpException
import java.io.IOException

sealed class RecipeError : Exception() {
    object NoNetwork : RecipeError()
    object QuotaExceeded : RecipeError()
    object Unauthorized : RecipeError()
    data class Unknown(override val cause: Throwable) : RecipeError()
}

fun Throwable.toUserMessage(context: Context): String = when (this) {
    is RecipeError.NoNetwork -> "No internet connection. Check your network and try again."
    is RecipeError.QuotaExceeded -> "Daily API limit reached. Please try again tomorrow."
    is RecipeError.Unauthorized -> "Authorization failed. Please contact support."
    else -> "Something went wrong. Please try again."
}

internal fun Exception.toRecipeError(): RecipeError = when {
    this is IOException -> RecipeError.NoNetwork
    this is HttpException && code() == 401 -> RecipeError.Unauthorized
    this is HttpException && code() == 402 -> RecipeError.QuotaExceeded
    else -> RecipeError.Unknown(this)
}
