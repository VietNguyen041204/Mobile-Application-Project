import android.R.attr.contentDescription
import android.R.id.message
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapp.greetingcard.AddCardScreen
import com.myapp.greetingcard.HomeScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.myapp.greetingcard.SearchCardsScreen
import com.myapp.greetingcard.StudyCardsScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.runtime.remember


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(navController: NavHostController) {
    //val navController = rememberNavController()

        var message by rememberSaveable {
            mutableStateOf("Welcome ")
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            // Removed topBar completely
            bottomBar = {
                BottomAppBar(
                    actions = {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "Message" },
                            textAlign = TextAlign.Center,
                            text = message
                        )
                    }
                )
            },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    title = {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                // modifier = Modifier.semantics { contentDescription = "user" },
                                text = "An Nam"
                            )
                        }
                    },
                    navigationIcon = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute != "home") {
                            Button(
                                onClick = {
                                    message = "Welcome"
                                    navController.navigateUp()
                                          },
                                modifier = Modifier.padding(start = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(text = "Back")
                            }
                        } else {
                            // no nav icon on home
                        }
                    }
                )
            }

//            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
//            floatingActionButton = { /* no FAB */ },
//            floatingActionButtonPosition = FabPosition.End,
//            containerColor = MaterialTheme.colorScheme.background,
//            contentColor = contentColorFor(MaterialTheme.colorScheme.background),
//            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
        )
        { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                // HOME
                composable(route = "home") {
                    HomeScreen(navigator = navController, changeMessage = { message = it })
                }
                // ADD CARD
                composable(route = "add_card") {
                    AddCardScreen(navigator = navController, changeMessage = {message = it})
                }
                //Study cards
                composable(route = "study_cards") {
                    StudyCardsScreen(navigator = navController)
                }
                //search cards
                composable(route = "search_cards") {
                    SearchCardsScreen(navigator = navController)
                }


            }

        }
    }