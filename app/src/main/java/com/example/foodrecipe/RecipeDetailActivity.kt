package com.example.foodrecipe

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.foodrecipe.data.model.RecipeDetail
import com.example.foodrecipe.repository.FavoritesRepository
import com.example.foodrecipe.repository.RecipeRepository
import kotlinx.coroutines.launch

class RecipeDetailActivity : ComponentActivity() {

    private val recipeRepository = RecipeRepository()
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var ivFavoriteToggle: ImageView

    private var loadedDetail: RecipeDetail? = null
    private var isFavorited = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        favoritesRepository = (applicationContext as App).favoritesRepository

        val recipeId = intent.getIntExtra("recipe_id", -1)
        if (recipeId == -1) {
            finish()
            return
        }

        val pbLoading = findViewById<ProgressBar>(R.id.pbDetailLoading)
        val scrollContent = findViewById<View>(R.id.scrollContent)
        ivFavoriteToggle = findViewById(R.id.ivFavoriteToggle)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        ivFavoriteToggle.setOnClickListener {
            val detail = loadedDetail ?: return@setOnClickListener
            lifecycleScope.launch {
                if (isFavorited) {
                    favoritesRepository.removeFavorite(detail.id)
                    isFavorited = false
                    Toast.makeText(this@RecipeDetailActivity, "Removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    favoritesRepository.addFavorite(detail)
                    isFavorited = true
                    Toast.makeText(this@RecipeDetailActivity, "Added to favorites", Toast.LENGTH_SHORT).show()
                }
                updateHeartVisual()
            }
        }

        lifecycleScope.launch {
            val result = recipeRepository.getRecipeDetail(recipeId)
            pbLoading.visibility = View.GONE
            result.onSuccess { detail ->
                scrollContent.visibility = View.VISIBLE
                populateDetail(detail)
                loadedDetail = detail
                isFavorited = favoritesRepository.isFavorite(detail.id)
                updateHeartVisual()
                ivFavoriteToggle.isEnabled = true
            }
            result.onFailure { error ->
                Toast.makeText(
                    this@RecipeDetailActivity,
                    "Failed to load recipe: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateHeartVisual() {
        if (isFavorited) {
            ivFavoriteToggle.setImageResource(android.R.drawable.btn_star_big_on)
            ivFavoriteToggle.setColorFilter(Color.parseColor("#D06D33"))
        } else {
            ivFavoriteToggle.setImageResource(android.R.drawable.btn_star_big_off)
            ivFavoriteToggle.setColorFilter(Color.parseColor("#0C573D"))
        }
    }

    private fun populateDetail(detail: RecipeDetail) {
        if (!detail.image.isNullOrEmpty()) {
            findViewById<ImageView>(R.id.ivDetailImage).load(detail.image)
        }

        findViewById<TextView>(R.id.tvDetailTitle).text = detail.title
        findViewById<TextView>(R.id.tvPrepTime).text = "${detail.readyInMinutes} min"
        findViewById<TextView>(R.id.tvServings).text = "${detail.servings} servings"

        val llIngredients = findViewById<LinearLayout>(R.id.llIngredients)
        detail.extendedIngredients.forEach { ingredient ->
            llIngredients.addView(TextView(this).apply {
                text = "• ${ingredient.original}"
                textSize = 14f
                setTextColor(Color.parseColor("#1D1D1D"))
                setPadding(0, 4, 0, 4)
            })
        }

        val llInstructions = findViewById<LinearLayout>(R.id.llInstructions)
        val steps = detail.analyzedInstructions.flatMap { it.steps }
        if (steps.isNotEmpty()) {
            steps.forEach { step ->
                llInstructions.addView(TextView(this).apply {
                    text = "${step.number}. ${step.step}"
                    textSize = 14f
                    setTextColor(Color.parseColor("#1D1D1D"))
                    setPadding(0, 6, 0, 6)
                })
            }
        } else if (!detail.instructions.isNullOrEmpty()) {
            llInstructions.addView(TextView(this).apply {
                text = Html.fromHtml(detail.instructions, Html.FROM_HTML_MODE_COMPACT)
                textSize = 14f
                setTextColor(Color.parseColor("#1D1D1D"))
            })
        }

        val nutritionMap = (detail.nutrition?.nutrients ?: emptyList()).associateBy { it.name }
        fun setNutrientValue(viewId: Int, name: String) {
            nutritionMap[name]?.let { n ->
                findViewById<TextView>(viewId).text = "${n.amount.toInt()} ${n.unit}"
            }
        }
        setNutrientValue(R.id.tvCaloriesValue, "Calories")
        setNutrientValue(R.id.tvProteinValue, "Protein")
        setNutrientValue(R.id.tvFatValue, "Fat")
        setNutrientValue(R.id.tvCarbsValue, "Carbohydrates")
    }
}
