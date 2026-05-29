package com.example.foodrecipe

import android.os.Bundle
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.foodrecipe.data.prefs.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : ComponentActivity() {

    private lateinit var userPrefs: UserPreferences
    private lateinit var rgDiet: RadioGroup
    private lateinit var cbDairy: CheckBox
    private lateinit var cbEgg: CheckBox
    private lateinit var cbGluten: CheckBox
    private lateinit var cbPeanut: CheckBox
    private lateinit var cbSeafood: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        userPrefs = (applicationContext as App).userPreferences

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        rgDiet = findViewById(R.id.rgDiet)
        cbDairy = findViewById(R.id.cbDairy)
        cbEgg = findViewById(R.id.cbEgg)
        cbGluten = findViewById(R.id.cbGluten)
        cbPeanut = findViewById(R.id.cbPeanut)
        cbSeafood = findViewById(R.id.cbSeafood)

        lifecycleScope.launch {
            val diet = userPrefs.dietFlow.first()
            val intolerances = userPrefs.intolerancesFlow.first()

            rgDiet.check(
                when (diet) {
                    "vegetarian" -> R.id.rbVegetarian
                    "vegan" -> R.id.rbVegan
                    "gluten free" -> R.id.rbGlutenFree
                    "dairy free" -> R.id.rbDairyFree
                    else -> R.id.rbNone
                }
            )

            cbDairy.isChecked = "dairy" in intolerances
            cbEgg.isChecked = "egg" in intolerances
            cbGluten.isChecked = "gluten" in intolerances
            cbPeanut.isChecked = "peanut" in intolerances
            cbSeafood.isChecked = "seafood" in intolerances

            // Register listeners after initial population to avoid spurious saves and toasts
            rgDiet.setOnCheckedChangeListener { _, checkedId ->
                val dietValue = when (checkedId) {
                    R.id.rbVegetarian -> "vegetarian"
                    R.id.rbVegan -> "vegan"
                    R.id.rbGlutenFree -> "gluten free"
                    R.id.rbDairyFree -> "dairy free"
                    else -> null
                }
                lifecycleScope.launch {
                    userPrefs.setDiet(dietValue)
                    Toast.makeText(this@SettingsActivity, R.string.preferences_saved, Toast.LENGTH_SHORT).show()
                }
            }

            val intoleranceSaver = CompoundButton.OnCheckedChangeListener { _, _ ->
                saveIntolerances()
            }
            cbDairy.setOnCheckedChangeListener(intoleranceSaver)
            cbEgg.setOnCheckedChangeListener(intoleranceSaver)
            cbGluten.setOnCheckedChangeListener(intoleranceSaver)
            cbPeanut.setOnCheckedChangeListener(intoleranceSaver)
            cbSeafood.setOnCheckedChangeListener(intoleranceSaver)
        }
    }

    private fun saveIntolerances() {
        val selected = buildSet {
            if (cbDairy.isChecked) add("dairy")
            if (cbEgg.isChecked) add("egg")
            if (cbGluten.isChecked) add("gluten")
            if (cbPeanut.isChecked) add("peanut")
            if (cbSeafood.isChecked) add("seafood")
        }
        lifecycleScope.launch {
            userPrefs.setIntolerances(selected)
            Toast.makeText(this@SettingsActivity, R.string.preferences_saved, Toast.LENGTH_SHORT).show()
        }
    }
}
