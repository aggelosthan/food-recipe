package com.example.foodrecipe

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            Toast.makeText(this, "Get Started clicked", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            Toast.makeText(this, "Log In clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
