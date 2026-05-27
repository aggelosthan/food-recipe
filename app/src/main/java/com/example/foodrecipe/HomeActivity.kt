package com.example.foodrecipe

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodrecipe.repository.RecipeRepository
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private val repository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        lifecycleScope.launch {
            val result = repository.searchRecipes("chicken")
            result.onSuccess { recipes ->
                recipes.forEach { recipe ->
                    Log.d("RecipeFetch", "id=${recipe.id}  title=${recipe.title}")
                }
            }
            result.onFailure { error ->
                Log.e("RecipeFetch", "Failed to fetch recipes", error)
            }
        }
    }
}
