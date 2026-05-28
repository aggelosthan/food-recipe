package com.example.foodrecipe

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class FavoritesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val tabBrowse = findViewById<LinearLayout>(R.id.tabBrowse)
        val tabPlanner = findViewById<LinearLayout>(R.id.tabPlanner)

        tabBrowse.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        tabPlanner.setOnClickListener {
            // Boilerplate hook for Planner navigation.
        }
    }
}
