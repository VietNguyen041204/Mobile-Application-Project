package com.myapp.greetingcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun StudyCardsScreen(
    getLesson: suspend (Int) -> List<FlashCard>
) {
    var lessonCards by remember { mutableStateOf(emptyList<FlashCard>()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) } // false = English, true = Vietnamese
    val scope = rememberCoroutineScope()

    // Load a new lesson when screen opens
    LaunchedEffect(Unit) {
        scope.launch {
            val cards = getLesson(3) // Get 3 random cards
            lessonCards = cards
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (lessonCards.isEmpty()) {
            Text("No cards available to study. Add some first!")
        } else {
            val currentCard = lessonCards[currentIndex]

            Text("Card ${currentIndex + 1} of ${lessonCards.size}", fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // The Flashcard
            Card(
                modifier = Modifier
                    .size(width = 300.dp, height = 200.dp)
                    .clickable {
                        // Toggle between English and Vietnamese
                        isFlipped = !isFlipped
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (isFlipped) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFlipped) currentCard.vietnameseCard ?: "?"
                        else currentCard.englishCard ?: "?",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Show NEXT button only if Vietnamese (isFlipped) is shown
            if (isFlipped) {
                Button(onClick = {
                    // Move to next card in a loop
                    currentIndex = (currentIndex + 1) % lessonCards.size
                    // Reset to English side
                    isFlipped = false
                }) {
                    Text("Next")
                }
            } else {
                // Invisible spacer to keep layout stable
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}