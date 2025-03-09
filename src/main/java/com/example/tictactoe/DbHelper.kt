package com.example.tictactoe

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DbHelper(context: Context) : SQLiteOpenHelper (context, "players", null , 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("""
            CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            firstName VARCHAR NOT NULL,
            lastName VARCHAR NOT NULL,
            email VARCHAR NOT NULL,
            password VARCHAR NOT NULL,
            highScore INTEGER NOT NULL DEFAULT 0,
            registrationDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun addUser(firstName: String, lastName: String, email: String, password: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("firstName", firstName)
        values.put("lastName", lastName)
        values.put("email", email)
        values.put("password", password)

        return db.insert("users", null, values)
    }

    fun updateHighScore(id: String, highScore: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply { put("highScore", highScore) }

        return db.update("users", values, "id=?", arrayOf(id))
    }

    fun getUserInfo(email: String, password: String): Pair<Long, Int>? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT id, highScore FROM users WHERE email=? AND password=?", arrayOf(email, password))

        if (!cursor.moveToFirst()) {
            return null
        }

        var pair: Pair<Long, Int>
        do {
            pair = Pair(
                cursor.getLong(0),
                cursor.getInt(1)
            )
        } while (false)

        cursor.close()
        return pair
    }

    fun getLeaderboard(): ArrayList<Triple<String, String, Int>> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT firstName, lastName, highScore FROM users ORDER BY highScore DESC", null)
        val list: ArrayList<Triple<String, String, Int>> = ArrayList()

        while (cursor.moveToNext()) {
            val player = Triple(cursor.getString(0), cursor.getString(1), cursor.getInt(2)) // Convert highScore to Int
            list.add(player)
        }

        cursor.close()
        return list
    }

    fun getlogin(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT email FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )

        val isLoggedIn = cursor.count > 0
        cursor.close()
        return isLoggedIn
    }


}