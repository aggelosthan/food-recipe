package com.example.foodrecipe

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class PlannerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

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
}
