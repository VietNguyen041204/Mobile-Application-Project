package com.myapp.greetingcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun EditCardScreen(
//    cardId: Int,
//    getFlashCardById: suspend (Int) -> FlashCard?,
    oldEnglish: String,
    oldVietnamese: String,
    updateFlashCard: suspend (String, String, String, String) -> Unit,
    navigateBack: () -> Unit,
    changeMessage: (String) -> Unit
) {
    var enWord by remember { mutableStateOf(oldEnglish) }
    var vnWord by remember { mutableStateOf(oldVietnamese) }
//    var currentUid by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

//    LaunchedEffect(cardId) {
//        val card = getFlashCardById(cardId)
//        if (card != null) {
//            currentUid = card.uid
//            enWord = card.englishCard ?: ""
//            vnWord = card.vietnameseCard ?: ""
//            changeMessage("Edit your card")
//        } else {
//            changeMessage("Error loading card")
//            navigateBack()
//        }
//    }

    LaunchedEffect(Unit) {
        changeMessage("Edit your card")
    }


    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = enWord,
            onValueChange = { enWord = it },
            label = { Text("English") },
            modifier = Modifier.semantics { contentDescription = "enTextField" }
        )
        TextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            label = { Text("Vietnamese") },
            modifier = Modifier.semantics { contentDescription = "vnTextField" }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        updateFlashCard(
                            oldEnglish,
                            oldVietnamese,
                            enWord,
                            vnWord
                        )
                        changeMessage("Card updated successfully")
                        navigateBack()
                    } catch (e: Exception) {
                        changeMessage("Update failed")
                    }
                }
            },
            modifier = Modifier.semantics { contentDescription = "Update" }
        ) {
            Text("Update")
        }
    }
}