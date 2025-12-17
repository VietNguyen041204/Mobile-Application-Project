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


@Serializable object Home
@Serializable object AddCard
@Serializable object StudyCards
@Serializable object SearchCards
@Serializable object Login
@Serializable data class EditCard(val english: String, val vietnamese: String)
// NEW: Token Route
@Serializable data class Token(val email: String)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(
    navController: NavHostController,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    var message by remember { mutableStateOf("") }
    val changeMessage = { text:String -> message = text }

    // Helpers
    val insertFlashCard: suspend (FlashCard) -> Unit = { flashCardDao.insertAll(it) }
    val getAllFlashCards: suspend () -> List<FlashCard> = { flashCardDao.getAll() }
    val deleteFlashCard: suspend (String, String) -> Unit = { eng, vn -> flashCardDao.deleteFlashCard(eng, vn) }
    val updateFlashCard: suspend (String, String, String, String) -> Unit = { oldEng, oldVn, newEng, newVn ->
        flashCardDao.updateByContent(oldEng, oldVn, newEng, newVn)
    }
    val getLesson: suspend (Int) -> List<FlashCard> = { size -> flashCardDao.getLesson(size) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Text("An Nam Study Room") } },
                navigationIcon = {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    // Logic to show Back button only if NOT on Home
                    if (currentRoute != null && !currentRoute.endsWith("Home")) {
                        Button(
                            modifier = Modifier.semantics{contentDescription="navigateBack"},
                            onClick = { navController.navigateUp() }) {
                            Text("Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Text(modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, text = message)
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            navController = navController,
            startDestination = Home
        ) {
            composable<Home> {
                HomeScreen(
                    navigateToAddCard = { navController.navigate(AddCard) },
                    navigateToStudyCards = { navController.navigate(StudyCards) },
                    navigateToSearchCards = { navController.navigate(SearchCards) },
                    navigateToLogin = { navController.navigate(Login) },
                    changeMessage = changeMessage
                )
            }
            composable<AddCard> {
                AddCardScreen(changeMessage, insertFlashCard)
            }
            composable<SearchCards> {
                SearchCardsScreen(
                    getAllFlashCards = getAllFlashCards,
                    onEditClick = { eng, vn -> navController.navigate(EditCard(eng, vn)) },
                    onDeleteClick = { eng, vn -> deleteFlashCard(eng, vn); changeMessage("Deleted") },
                    changeMessage = changeMessage
                )
            }
            composable<EditCard> { backStackEntry ->
                val args = backStackEntry.toRoute<EditCard>()
                EditCardScreen(
                    oldEnglish = args.english,
                    oldVietnamese = args.vietnamese,
                    updateFlashCard = updateFlashCard,
                    navigateBack = { navController.navigateUp() },
                    changeMessage = changeMessage
                )
            }
            // NEW: Login Screen
            composable<Login> {
                LoginScreen(
                    networkService = networkService,
                    changeMessage = changeMessage,
                    navigateToken = { email -> navController.navigate(Token(email)) }
                )
            }
            // NEW: Token Screen
            composable<Token> { backStackEntry ->
                val args = backStackEntry.toRoute<Token>()
                TokenScreen(
                    email = args.email,
                    changeMessage = changeMessage,
                    navigateToHome = { navController.navigate(Home) }
                )
            }
            // NEW: Study Cards
            composable<StudyCards> {
                StudyCardsScreen(getLesson = getLesson)
            }
        }
    }
}