package com.example.foodrecipe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val btnTabLogin = findViewById<Button>(R.id.btnTabLogin)
        val btnTabSignUp = findViewById<Button>(R.id.btnTabSignUp)
        val loginForm = findViewById<LinearLayout>(R.id.loginForm)
        val signUpForm = findViewById<LinearLayout>(R.id.signUpForm)
        val btnSignUpSubmit = findViewById<Button>(R.id.btnSignUpSubmit)

        btnTabLogin.setOnClickListener {
            loginForm.visibility = View.VISIBLE
            signUpForm.visibility = View.GONE
        }

        btnTabSignUp.setOnClickListener {
            loginForm.visibility = View.GONE
            signUpForm.visibility = View.VISIBLE
        }

        btnSignUpSubmit.setOnClickListener {
            Toast.makeText(this, "Sign up clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
