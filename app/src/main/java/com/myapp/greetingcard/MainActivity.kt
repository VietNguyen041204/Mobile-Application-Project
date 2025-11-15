package com.myapp.greetingcard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val db = Room.databaseBuilder(
                    applicationContext,
                    AnNamDatabase::class.java, "An Nam database"
                ).build()
                val flashCardDao = db.flashCardDao()
                //flashCardDao.insertAll(FlashCard(uid = 0, englishCard = "Hello", vietnameseCard = "Xin chào"))
                //testing
                runBlocking {

                    val flashCard = flashCardDao.getAll()
                    Log.d("AnNam",flashCard.toString() )

                }
                //Navigator()
                Navigator(navController, flashCardDao)
            }
        }
    }
}