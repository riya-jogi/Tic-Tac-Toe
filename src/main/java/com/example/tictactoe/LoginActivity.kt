package com.example.tictactoe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    private lateinit var loginEmail: EditText
    private lateinit var loginPassword: EditText
    private lateinit var login: Button
    private lateinit var register: Button
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPassword = findViewById(R.id.loginPassword)
        loginEmail = findViewById(R.id.loginEmail)
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        prefs = this.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        login.setOnClickListener {
            val email = loginEmail.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            val helper = DbHelper(this)

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter values", Toast.LENGTH_LONG).show()
            } else {
                val isValidUser = helper.getlogin(email, password)

                if (isValidUser) {
                    val res = helper.getUserInfo(email, password) // Assuming this returns a Pair<Long, Int> (userId, highScore)
                    val editor = prefs.edit()

                    res?.let {
                        editor.putLong("userId", it.first)
                        editor.putInt("highScore", it.second)
                        editor.apply()
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("lemail", email)
                    startActivity(intent)
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
                }
            }
        }


        register.setOnClickListener {
            intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}