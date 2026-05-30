package com.example.foodrecipe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrecipe.repository.RecipeRepository
import com.example.foodrecipe.repository.toUserMessage
import com.example.foodrecipe.ui.home.RecipeAdapter
import kotlinx.coroutines.launch

class PlannerActivity : ComponentActivity() {

    private val repository by lazy {
        RecipeRepository((applicationContext as App).userPreferences)
    }
    private lateinit var adapter: RecipeAdapter
    private lateinit var rvMealIdeas: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmptyState: TextView

    private lateinit var chipBreakfast: LinearLayout
    private lateinit var chipLunch: LinearLayout
    private lateinit var chipDinner: LinearLayout
    private var selectedMealType = "breakfast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

        rvMealIdeas = findViewById(R.id.rvMealIdeas)
        pbLoading = findViewById(R.id.pbLoading)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        chipBreakfast = findViewById(R.id.chipBreakfast)
        chipLunch = findViewById(R.id.chipLunch)
        chipDinner = findViewById(R.id.chipDinner)

        adapter = RecipeAdapter { recipe ->
            startActivity(
                Intent(this, RecipeDetailActivity::class.java)
                    .putExtra("recipe_id", recipe.id)
            )
        }
        rvMealIdeas.layoutManager = LinearLayoutManager(this)
        rvMealIdeas.adapter = adapter

        chipBreakfast.setOnClickListener { selectMealType("breakfast") }
        chipLunch.setOnClickListener { selectMealType("lunch") }
        chipDinner.setOnClickListener { selectMealType("dinner") }

        selectMealType("breakfast")

        findViewById<LinearLayout>(R.id.tabBrowse).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        findViewById<LinearLayout>(R.id.tabFavorites).setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
        }

        // tabPlanner is already active — no-op
    }

    private fun selectMealType(type: String) {
        selectedMealType = type
        updateChipSelection()
        loadMealIdeas(type)
    }

    private fun updateChipSelection() {
        val chips = mapOf("breakfast" to chipBreakfast, "lunch" to chipLunch, "dinner" to chipDinner)
        chips.forEach { (type, chip) ->
            val isSelected = type == selectedMealType
            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_chip_selected else R.drawable.bg_category_chip
            )
            val label = chip.getChildAt(0) as TextView
            label.setTextColor(
                if (isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#2B2B2B")
            )
        }
    }

    private fun loadMealIdeas(type: String) {
        pbLoading.visibility = View.VISIBLE
        rvMealIdeas.visibility = View.GONE
        tvEmptyState.visibility = View.GONE

        lifecycleScope.launch {
            val result = repository.searchRecipes(query = "", type = type)
            pbLoading.visibility = View.GONE
            result.onSuccess { recipes ->
                if (recipes.isEmpty()) {
                    tvEmptyState.visibility = View.VISIBLE
                } else {
                    rvMealIdeas.visibility = View.VISIBLE
                    adapter.submitList(recipes)
                }
            }
            result.onFailure { error ->
                Toast.makeText(
                    this@PlannerActivity,
                    error.toUserMessage(this@PlannerActivity),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
