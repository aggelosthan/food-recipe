package com.example.foodrecipe

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
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

    private lateinit var chipBeef: LinearLayout
    private lateinit var chipChicken: LinearLayout
    private lateinit var chipDessert: LinearLayout
    private lateinit var chipSeafood: LinearLayout
    private lateinit var chipVeg: LinearLayout
    private var selectedChipId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvRecipes = findViewById(R.id.rvFeaturedRecipes)
        pbLoading = findViewById(R.id.pbLoading)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        chipBeef = findViewById(R.id.chipBeef)
        chipChicken = findViewById(R.id.chipChicken)
        chipDessert = findViewById(R.id.chipDessert)
        chipSeafood = findViewById(R.id.chipSeafood)
        chipVeg = findViewById(R.id.chipVeg)

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
                selectChip(null)
                loadRecipes(query.ifBlank { "chicken" })
                true
            } else {
                false
            }
        }

        chipBeef.setOnClickListener { onChipTapped(it.id, "beef", etSearch) }
        chipChicken.setOnClickListener { onChipTapped(it.id, "chicken", etSearch) }
        chipDessert.setOnClickListener { onChipTapped(it.id, "dessert", etSearch) }
        chipSeafood.setOnClickListener { onChipTapped(it.id, "seafood", etSearch) }
        chipVeg.setOnClickListener { onChipTapped(it.id, "vegetarian", etSearch) }

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

    private fun onChipTapped(chipId: Int, query: String, etSearch: EditText) {
        etSearch.setText("")
        selectChip(chipId)
        loadRecipes(query)
    }

    private fun selectChip(chipId: Int?) {
        val chips = listOf(chipBeef, chipChicken, chipDessert, chipSeafood, chipVeg)
        for (chip in chips) {
            val isSelected = chip.id == chipId
            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_chip_selected else R.drawable.bg_category_chip
            )
            val icon = chip.getChildAt(0) as ImageView
            val label = chip.getChildAt(1) as TextView
            icon.setColorFilter(
                if (isSelected) Color.parseColor("#E8F3EB") else Color.parseColor("#4B5A4F")
            )
            label.setTextColor(
                if (isSelected) Color.parseColor("#FFFFFF") else Color.parseColor("#2B2B2B")
            )
        }
        selectedChipId = chipId
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
