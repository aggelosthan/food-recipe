package com.example.foodrecipe

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
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
import com.example.foodrecipe.repository.toUserMessage
import kotlinx.coroutines.launch

class RecipeDetailActivity : ComponentActivity() {

    private val recipeRepository = RecipeRepository()
    private lateinit var favoritesRepository: FavoritesRepository
    private lateinit var ivFavoriteToggle: ImageView
    private lateinit var ivShareButton: ImageView

    private var loadedDetail: RecipeDetail? = null
    private var isFavorited = false
    private val checkedIngredients = mutableSetOf<Int>()

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
        ivShareButton = findViewById(R.id.ivShareButton)

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

        ivShareButton.setOnClickListener { shareIngredients() }

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
                ivShareButton.isEnabled = true
            }
            result.onFailure { error ->
                Toast.makeText(
                    this@RecipeDetailActivity,
                    error.toUserMessage(this@RecipeDetailActivity),
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
        val greenTint = ColorStateList.valueOf(Color.parseColor("#0C573D"))
        detail.extendedIngredients.forEach { ingredient ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 6, 0, 6)
            }

            val cb = CheckBox(this).apply {
                isChecked = ingredient.id in checkedIngredients
                buttonTintList = greenTint
            }

            val tv = TextView(this).apply {
                text = ingredient.original
                textSize = 14f
                setTextColor(Color.parseColor("#1D1D1D"))
                setPadding(8, 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            applyIngredientStyle(tv, cb.isChecked)

            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) checkedIngredients.add(ingredient.id) else checkedIngredients.remove(ingredient.id)
                applyIngredientStyle(tv, isChecked)
            }

            row.addView(cb)
            row.addView(tv)
            llIngredients.addView(row)
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

    private fun applyIngredientStyle(tv: TextView, isChecked: Boolean) {
        tv.paintFlags = if (isChecked) {
            tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        tv.alpha = if (isChecked) 0.5f else 1f
    }

    private fun shareIngredients() {
        val detail = loadedDetail ?: return
        val lines = detail.extendedIngredients.joinToString("\n") { "- ${it.original}" }
        val text = "Ingredients for ${detail.title}\n\n$lines\n\nFrom SavorySteps"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Ingredients for ${detail.title}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(intent, "Share ingredients via"))
    }
}
