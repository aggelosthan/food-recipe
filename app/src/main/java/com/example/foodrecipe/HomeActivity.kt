package com.example.foodrecipe

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var rvRecipes: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvRecipes = findViewById(R.id.rvFeaturedRecipes)
        pbLoading = findViewById(R.id.pbLoading)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        adapter = RecipeAdapter { recipe ->
            startActivity(
                Intent(this, RecipeDetailActivity::class.java)
                    .putExtra("recipe_id", recipe.id)
            )
        }
        rvRecipes.layoutManager = LinearLayoutManager(this)
        rvRecipes.adapter = adapter

        etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = v.text.toString().trim()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                loadRecipes(query.ifBlank { "chicken" })
                true
            } else {
                false
            }
        }

        loadRecipes("chicken")

        val tabFavorites = findViewById<LinearLayout>(R.id.tabFavorites)
        val tabPlanner = findViewById<LinearLayout>(R.id.tabPlanner)

        tabFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        tabPlanner.setOnClickListener {
            // Boilerplate hook for Planner navigation.
        }
    }

    private fun loadRecipes(query: String) {
        pbLoading.visibility = View.VISIBLE
        rvRecipes.visibility = View.GONE
        tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            val result = repository.searchRecipes(query)
            pbLoading.visibility = View.GONE
            result.onSuccess { recipes ->
                if (recipes.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                } else {
                    rvRecipes.visibility = View.VISIBLE
                    adapter.submitList(recipes)
                }
            }
            result.onFailure { error ->
                Toast.makeText(
                    this@HomeActivity,
                    "Failed to load recipes: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
