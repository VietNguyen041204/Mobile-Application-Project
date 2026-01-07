package com.myapp.greetingcard

import android.util.Base64
import androidx.annotation.OptIn
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
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.semantics.contentDescription
//import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

@OptIn(UnstableApi::class)
@Composable
fun StudyCardsScreen(
    getAllFlashCards: suspend () -> List<FlashCard>,
    networkService: NetworkService,
    changeMessage: (String) -> Unit
) {
    var lessonCards by remember { mutableStateOf(emptyList<FlashCard>()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) } // false = English, true = Vietnamese

    var hasAudioFile by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    val context = LocalContext.current
//    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            lessonCards = getAllFlashCards()

            val preferences = context.dataStore.data.first()
            email = preferences[EMAIL] ?: ""
            token = preferences[TOKEN] ?: ""
        }
    }

    //Hashing the filename
    fun getHashedFilename(word: String): String { // copy: how to hash the filename (dealing wiith file system which doesnt approve UTF8)
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(word.toByteArray())
            val hashString = bytes.joinToString("") { "%02x".format(it) }
            "$hashString.mp3"
        } catch (e: Exception) {
            "fallback_filename.mp3"
        }
    }

    fun checkAudioFile(vietnameseWord: String) {
        if (vietnameseWord.isBlank()) return
        val file = File(context.filesDir, getHashedFilename(vietnameseWord))
        hasAudioFile = file.exists()
    }

    fun saveAudioToInternalStorage(base64String: String, vietnameseWord: String) {
        try {
            val audioData = Base64.decode(base64String, Base64.DEFAULT)
            val file = File(context.filesDir, getHashedFilename(vietnameseWord))
            FileOutputStream(file).use { fos ->
                fos.write(audioData)
            }
            hasAudioFile = true
            changeMessage("Audio saved successfully!")
        } catch (e: Exception) {
            changeMessage("Error saving: ${e.message}")
        }
    }

    fun playAudio(vietnameseWord: String) {
        val file = File(context.filesDir, getHashedFilename(vietnameseWord))

        if (!file.exists()) {
            changeMessage("Audio file not found")
            return
        }

        try {
            val player = ExoPlayer.Builder(context).build()
            val mediaItem = MediaItem.fromUri(file.toUri())

            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        player.release()
                    }
                }
            })

            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            changeMessage("Error playing audio: ${e.message}")
        }
    }


    val currentCard = if (lessonCards.isNotEmpty()) lessonCards[currentIndex] else null

    LaunchedEffect(isFlipped, currentIndex) {
        if (isFlipped && currentCard?.vietnameseCard != null) {
            checkAudioFile(currentCard.vietnameseCard)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (lessonCards.isNotEmpty() && currentCard != null) {
            Text("Card ${currentIndex + 1} of ${lessonCards.size}")

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .size(width = 300.dp, height = 200.dp)
                    .clickable {
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

            if (isFlipped) {
                val vnWord = currentCard.vietnameseCard ?: ""

                if (hasAudioFile) {
                    // Scenario A: We have the file -> Show Play
                    Button(onClick = { playAudio(vnWord) }) {
                        Text("Play Audio")
                    }
                } else {
                    // Scenario B: No file -> Show Generate
                    Button(
                        enabled = !isGenerating,
                        onClick = {
                            if (email.isBlank() || token.isBlank()) {
                                changeMessage("Please Log In first!")
                            } else {
                                scope.launch {
                                    isGenerating = true
                                    changeMessage("Generating audio...")
                                    try {
                                        val response = withContext(Dispatchers.IO) {
                                            networkService.generateAudio(
                                                request = AudioRequest(vnWord, email, token)
                                            )
                                        }

                                        if (response.code == 200) {
                                            saveAudioToInternalStorage(response.message, vnWord)
                                        } else {
                                            changeMessage("API Error: ${response.message}")
                                        }
                                    } catch (e: Exception) {
                                        changeMessage("Network Error: ${e.message}")
                                    } finally {
                                        isGenerating = false
                                    }
                                }
                            }
                        }
                    ) {
                        Text(if (isGenerating) "Generating..." else "Generate Audio")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    currentIndex = (currentIndex + 1) % lessonCards.size
                    isFlipped = false
                    hasAudioFile = false
                }) {
                    Text("Next")
                }
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            Text("No cards found...")
        }
    }
}