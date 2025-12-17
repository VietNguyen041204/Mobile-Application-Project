//package com.myapp.greetingcard
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.semantics.contentDescription
//import androidx.compose.ui.semantics.semantics
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//
//@Composable
//fun ShowCardScreen(
//    cardId: Int,
//    getFlashCardById: suspend (Int) -> FlashCard?,
//    deleteFlashCard: suspend (FlashCard) -> Unit,
//    navigateBack: () -> Unit,
//    changeMessage: (String) -> Unit
//) {
//    var card by remember { mutableStateOf<FlashCard?>(null) }
//    val scope = rememberCoroutineScope()
//
//    LaunchedEffect(cardId) {
//        card = getFlashCardById(cardId)
//        if (card == null) {
//            changeMessage("Error: Card not found")
//            navigateBack()
//        } else {
//            changeMessage("Showing card details")
//        }
//    }
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        card?.let {
//            TextField(
//                value = it.englishCard ?: "",
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("en") },
//                modifier = Modifier.semantics { contentDescription = "enText" }
//            )
//            TextField(
//                value = it.vietnameseCard ?: "",
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("vn") },
//                modifier = Modifier.semantics { contentDescription = "vnText" }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    scope.launch {
//                        deleteFlashCard(it)
//                        changeMessage("Card deleted successfully")
//                        navigateBack()
//                    }
//                },
//                modifier = Modifier.semantics { contentDescription = "Delete" },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text("Delete")
//            }
//        } ?: run {
//            Text("Loading card...")
//        }
//    }
//}