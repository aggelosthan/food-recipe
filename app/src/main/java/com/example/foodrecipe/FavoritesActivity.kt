package com.example.foodrecipe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipe.data.model.RecipeSummary
import com.example.foodrecipe.repository.FavoritesRepository
import com.example.foodrecipe.ui.home.RecipeAdapter
import kotlinx.coroutines.launch

class FavoritesActivity : ComponentActivity() {

    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var adapter: RecipeAdapter
    private lateinit var rvFavorites: RecyclerView
    private lateinit var emptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        favoritesRepository = (applicationContext as App).favoritesRepository

        rvFavorites = findViewById(R.id.rvFavorites)
        emptyState = findViewById(R.id.emptyState)

        adapter = RecipeAdapter { recipe ->
            startActivity(
                Intent(this, RecipeDetailActivity::class.java)
                    .putExtra("recipe_id", recipe.id)
            )
        }
        rvFavorites.layoutManager = LinearLayoutManager(this)
        rvFavorites.adapter = adapter

        val tabBrowse = findViewById<LinearLayout>(R.id.tabBrowse)
        val tabPlanner = findViewById<LinearLayout>(R.id.tabPlanner)

        tabBrowse.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        tabPlanner.setOnClickListener {
            // Boilerplate hook for Planner navigation.
        }

        findViewById<Button>(R.id.btnBrowse).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            favoritesRepository.observeAll().collect { favorites ->
                if (favorites.isEmpty()) {
                    rvFavorites.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                } else {
                    emptyState.visibility = View.GONE
                    rvFavorites.visibility = View.VISIBLE
                    adapter.submitList(favorites.map { entity ->
                        RecipeSummary(
                            id = entity.id,
                            title = entity.title,
                            image = entity.image,
                            imageType = "jpg"
                        )
                    })
                }
            }
        }
    }
}
