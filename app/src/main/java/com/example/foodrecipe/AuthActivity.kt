package com.example.foodrecipe

import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
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
        val btnLoginSubmit = findViewById<Button>(R.id.btnLoginSubmit)
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)

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
            startActivity(Intent(this, HomeActivity::class.java))
        }

        btnLoginSubmit.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString()

            if (email == "test" && password == "test") {
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "Use dummy account: test / test",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
