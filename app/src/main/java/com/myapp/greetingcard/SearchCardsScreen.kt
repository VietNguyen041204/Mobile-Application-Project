package com.myapp.greetingcard

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
@Composable
fun FlashCardList(
//    selectedItem: (FlashCard) -> Unit,
    flashCards: List<FlashCard>,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color.LightGray)
                    .padding(6.dp),
//                    .clickable(onClick = {
//                        selectedItem(flashCard)
//                    }
//                    )
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                Column(modifier = Modifier.padding(6.dp))
//                { Text(flashCard.englishCard.toString()) }
//                Column(modifier = Modifier.padding(6.dp)) { Text(" = ") }
//                Column(modifier = Modifier.padding(6.dp))
//                { Text(flashCard.vietnameseCard.toString()) }
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(6.dp))
                    { Text(flashCard.englishCard ?: "") }
                    Column(modifier = Modifier.padding(6.dp)) { Text(" = ") }
                    Column(modifier = Modifier.padding(6.dp))
                    { Text(flashCard.vietnameseCard ?: "") }
                }
                Row {
                    IconButton(onClick = {onEditClick(flashCard.englishCard ?: "", flashCard.vietnameseCard ?: "")}) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue
                        )
                    }
                    IconButton(onClick = {onDeleteClick(flashCard.englishCard ?: "", flashCard.vietnameseCard ?: "")}) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchCardsScreen(
//    selectedItem: (FlashCard) -> Unit,
    getAllFlashCards: suspend () -> List<FlashCard>,
    onEditClick: (String, String) -> Unit,
    onDeleteClick: suspend (String, String) -> Unit,
    changeMessage: (String) -> Unit
) {
    var flashCards by remember { mutableStateOf(emptyList<FlashCard>()) }
    val scope = rememberCoroutineScope()

    fun loadData(){
        scope.launch {
            flashCards = getAllFlashCards()
            changeMessage("Found ${flashCards.size} cards")
        }
    }

    LaunchedEffect(Unit) {
//        flashCards = getAllFlashCards()
        loadData()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(
            modifier = Modifier.size(16.dp)
        )
        FlashCardList(
            flashCards = flashCards,
            onEditClick = onEditClick,
            onDeleteClick = { eng, vn ->
                scope.launch {
                    onDeleteClick(eng, vn)
                    loadData()
                }
            }
//            selectedItem = selectedItem
        )
    }
}