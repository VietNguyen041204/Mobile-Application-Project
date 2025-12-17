package com.myapp.greetingcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.util.prefs.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToAddCard: () -> Unit,
    navigateToStudyCards: () -> Unit,
    navigateToSearchCards: () -> Unit,
    navigateToLogin: () -> Unit,
    changeMessage: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    val context = LocalContext.current
    val appContext = context.applicationContext
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val preferencesFlow: Flow<androidx.datastore.preferences.core.Preferences> = appContext.dataStore.data
        val preferences = preferencesFlow.first()
        changeMessage(preferences[EMAIL] ?: "")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        Button(modifier = Modifier.semantics{contentDescription="navigateToStudyCards"},
            onClick = {
                navigateToStudyCards()
            })
        { Text("Study Cards", modifier = Modifier.semantics{contentDescription="StudyCards"},) }
        Button(modifier = Modifier.semantics{contentDescription="navigateToAddCard"},
            onClick = {
                navigateToAddCard()
            }) {
            Text("Add Card", modifier = Modifier.semantics{contentDescription="AddCard"},)
        }
        Button(modifier = Modifier.semantics{contentDescription="navigateToSearchCards"},
            onClick = {
            navigateToSearchCards()
        }) {
            Text("Search Cards", modifier = Modifier.semantics{contentDescription="SearchCards"},)
        }
        Button(
            modifier = Modifier.semantics { contentDescription = "navigateToLogin" },
            onClick = {
                navigateToLogin()
            }
        ) {
            Text("Log In", modifier = Modifier.semantics{contentDescription="SearchCards"},)
        }
        Button(
            modifier = Modifier
                .semantics { contentDescription = "ExecuteLogout" }, onClick = {

                scope.launch {
                    appContext.dataStore.edit { preferences ->
                        preferences.remove(EMAIL)
                        preferences.remove(TOKEN)
                        changeMessage(preferences[EMAIL] ?: "")
                    }
                }
            }) {
            Text(
                "Log out",
                modifier = Modifier.semantics { contentDescription = "Logout" }
            )
        }
    }
}

private fun CoroutineScope.changeMessage(p1: String) {}


