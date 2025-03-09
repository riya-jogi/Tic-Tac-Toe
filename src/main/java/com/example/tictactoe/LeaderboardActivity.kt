package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var leaderboardListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)
        leaderboardListView = findViewById(R.id.leaderboardListView)
        val helper = DbHelper(this)
        val arrayList = ArrayList<String>()
        for (player in helper.getLeaderboard()) {
            arrayList.add("${player.first} ${player.second} --> ${player.third}")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList)

        leaderboardListView.adapter = adapter
    }
}