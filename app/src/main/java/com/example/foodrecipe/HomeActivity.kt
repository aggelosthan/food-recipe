package com.example.foodrecipe

import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipe.repository.RecipeRepository
import com.example.foodrecipe.ui.home.RecipeAdapter
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private val repository = RecipeRepository()
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val rvRecipes = findViewById<RecyclerView>(R.id.rvFeaturedRecipes)
        val pbLoading = findViewById<ProgressBar>(R.id.pbLoading)

        adapter = RecipeAdapter { recipe ->
            Log.d("RecipeClick", "Clicked: ${recipe.title}")
        }
        rvRecipes.layoutManager = LinearLayoutManager(this)
        rvRecipes.adapter = adapter

        lifecycleScope.launch {
            val result = repository.searchRecipes("chicken")
            pbLoading.visibility = View.GONE
            result.onSuccess { recipes ->
                rvRecipes.visibility = View.VISIBLE
                adapter.submitList(recipes)
            }
            result.onFailure { error ->
                Log.e("RecipeFetch", "Failed to load recipes", error)
                Toast.makeText(
                    this@HomeActivity,
                    "Failed to load recipes: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val tabFavorites = findViewById<LinearLayout>(R.id.tabFavorites)
        val tabPlanner = findViewById<LinearLayout>(R.id.tabPlanner)

        tabFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        tabPlanner.setOnClickListener {
            // Boilerplate hook for Planner navigation.
        }
    }
}
