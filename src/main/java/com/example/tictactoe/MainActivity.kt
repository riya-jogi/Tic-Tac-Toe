package com.example.tictactoe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var buttons: Array<Array<Button>>
    private var currentPlayer = 'O'
    private var playerWins = 0
    private var botWins = false
    private lateinit var prefs: SharedPreferences
    private lateinit var highScoreTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var leaderboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        highScoreTextView = findViewById(R.id.highScoreTextView)
        logoutButton = findViewById(R.id.logout)
        leaderboard = findViewById(R.id.leaderboard)

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("userId")) {
            intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        highScoreTextView.text = "High Score: ${prefs.getInt("highScore", -1)}"

        initializeButtons()
        currentPlayer = 'O'
        btnreset1.setOnClickListener()
        {
            resetBoard()
        }
        logoutButton.setOnClickListener {
            val editor = prefs.edit()
            editor.clear().apply()

            intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        leaderboard.setOnClickListener {
            intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeButtons() {
        buttons = Array(3) { row ->
            Array(3) { col ->
                findViewById<Button>(resources.getIdentifier("btn$row$col", "id", packageName)).apply {
                    setOnClickListener {
                        onButtonClick(row, col)
                    }
                }
            }
        }
    }

    private fun onButtonClick(row: Int, col: Int) {
        val button = buttons[row][col]
        if (button.text.isEmpty()) {
            button.text = currentPlayer.toString()
            button.setTextColor(if (currentPlayer == 'O') Color.rgb(241,77,26) else Color.rgb(63,81,181)) // Set color
            if (checkWin(row, col, currentPlayer)) {
                if (currentPlayer == 'O') {
                    playerWins++
                    showToast("Player wins: $playerWins")
                } else {
                    botWins = true
                    showToast("Player X wins!\nBot wins")
                }
                updateHighScore()
                resetBoard()
            } else if (checkDraw()) {
                showToast("It's a draw!\nPlayer wins: $playerWins")
                resetBoard()
            } else {
                switchPlayer()
                botMove()
            }
        } else {
            showToast("Invalid move! Try again.")
        }
    }

    private fun botMove() {
        for (i in 0..2) {
            for (j in 0..2) {
                if (buttons[i][j].text.isEmpty()) {
                    buttons[i][j].text = currentPlayer.toString()
                    buttons[i][j].setTextColor(Color.rgb(63,81,181)) // Bot move color

                    if (checkWin(i, j, currentPlayer)) {
                        processWin()
                        return
                    }
                    buttons[i][j].text = "" // Reset if not winning move
                }
            }
        }

        // If no winning move, make a random move
        var row: Int
        var col: Int
        do {
            row = (0..2).random()
            col = (0..2).random()
        } while (buttons[row][col].text.isNotEmpty())

        buttons[row][col].text = currentPlayer.toString()
        buttons[row][col].setTextColor(Color.rgb(63,81,181)) // Set bot's move color

        if (checkWin(row, col, currentPlayer)) {
            processWin()
        } else if (checkDraw()) {
            showToast("It's a draw!")
            resetBoard()
        } else {
            switchPlayer()
        }
    }

    private fun processWin() {
        if (currentPlayer == 'O') {
            playerWins++
            showToast("Player wins: $playerWins")
        } else {
            botWins = true
            showToast("Bot wins!")
        }
        updateHighScore()
        resetBoard()
    }

    private fun checkWin(row: Int, col: Int, player: Char): Boolean {
        return (buttons[row][0].text == player.toString() && buttons[row][1].text == player.toString() && buttons[row][2].text == player.toString()) ||
                (buttons[0][col].text == player.toString() && buttons[1][col].text == player.toString() && buttons[2][col].text == player.toString()) ||
                (buttons[0][0].text == player.toString() && buttons[1][1].text == player.toString() && buttons[2][2].text == player.toString()) ||
                (buttons[0][2].text == player.toString() && buttons[1][1].text == player.toString() && buttons[2][0].text == player.toString())
    }

    private fun checkDraw(): Boolean {
        return buttons.all { row ->
            row.all { button ->
                button.text.isNotEmpty()
            }
        }
    }

    private fun resetBoard() {
        buttons.forEach { row ->
            row.forEach { button ->
                button.text = ""
            }
        }
        currentPlayer = 'O'
        if (botWins) {
            playerWins = 0 // Reset player's win count if bot wins
            botWins = false
        }
    }

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == 'O') 'X' else 'O'
    }

    private fun updateHighScore() {
        val currentHighScore = prefs.getInt("highScore", 0)
        if (playerWins > currentHighScore) {
            val editor = prefs.edit()
            editor.putInt("highScore", playerWins)
            editor.apply()

            // Update in database
            val dbHelper = DbHelper(this)
            dbHelper.updateHighScore(prefs.getLong("userId", -1L).toString(), playerWins.toString())

            // Refresh UI
            highScoreTextView.text = "High Score: $playerWins"
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}