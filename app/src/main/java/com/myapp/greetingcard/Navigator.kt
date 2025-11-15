package com.myapp.greetingcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState


@Serializable
object Home

@Serializable
object AddCard

@Serializable
object StudyCards

@Serializable
object SearchCards

@Serializable
data class ShowCard(val cardId: Int)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    navController: NavHostController,
    flashCardDao: FlashCardDao
) {
    var message by remember { mutableStateOf("") }
    val navigateToAddCard = fun() {
        navController.navigate(AddCard)
    }
    val navigateToStudyCards = fun() {
        navController.navigate(StudyCards)
    }
    val navigateToSearchCards = fun() {
        navController.navigate(SearchCards)
    }
    val changeMessage = fun(text:String){
        message = text
    }

    val insertFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.insertAll(flashCard)
    }

    val getAllFlashCards: suspend () -> List<FlashCard> = {
        flashCardDao.getAll()
    }

    val deleteFlashCard: suspend (FlashCard) -> Unit = { flashCard ->
        flashCardDao.delete(flashCard)
    }

    val getFlashCardById: suspend (Int) -> FlashCard? = { id ->
        flashCardDao.getCardById(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "An Nam Study Room"
                        )
                    }
                },
                navigationIcon = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    if (currentRoute != null && currentRoute != navController.graph.findStartDestination().route) {
                        Button(
                            modifier = Modifier.semantics{contentDescription="navigateBack"},
                            onClick = {
                                navController.navigateUp()
                            }) {
                            Text("Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Message"
                            },
                        textAlign = TextAlign.Center,
                        text = message
                    )
                })
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding)
                .fillMaxWidth(),
            navController = navController,
            startDestination = Home
        ) {
            // HOME
            composable<Home> {
                HomeScreen(
                    changeMessage = changeMessage,
                    navigateToAddCard = navigateToAddCard,
                    navigateToStudyCards = navigateToStudyCards,
                    navigateToSearchCards = navigateToSearchCards
                )
            }
            // ADD CARD
            composable<AddCard> {
                AddCardScreen(
                    changeMessage = changeMessage,
                    insertFlashCard = insertFlashCard
                )
            }
            // STUDY CARDS
            composable<StudyCards> {
                StudyCardsScreen(
                )
            }
            // SEARCH CARDS
            composable<SearchCards> {
                SearchCardsScreen(
                    getAllFlashCards = getAllFlashCards,
                    selectedItem = { flashCard ->
                        navController.navigate(ShowCard(flashCard.uid))
                    }
                )
            }

            // SHOW CARD SCREEN
            composable<ShowCard> { backStackEntry ->
                val args = backStackEntry.toRoute<ShowCard>()  // Get the type-safe arguments
                ShowCardScreen(
                    cardId = args.cardId,
                    getFlashCardById = getFlashCardById,
                    deleteFlashCard = deleteFlashCard,
                    navigateBack = { navController.navigateUp() },
                    changeMessage = changeMessage
                )
            }
        }
    }
}