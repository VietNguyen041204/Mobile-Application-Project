package com.myapp.greetingcard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    networkService: NetworkService,
    changeMessage: (String) -> Unit,
    navigateToken: (String) -> Unit // Definition
) {
    var email by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("email") },
            modifier = Modifier.semantics { contentDescription = "emailInput" }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.semantics { contentDescription = "Enter" },
            onClick = {
                scope.launch {
                    try {
                        changeMessage("Requesting token...")
                        val result = withContext(Dispatchers.IO) {
                            networkService.generateToken(email = UserCredential(email))
                        }

                        if (result.code == 200) {
                            changeMessage(result.message)
                            navigateToken(email)
                        } else {
                            changeMessage("Error: ${result.message}")
                        }
                    } catch (e: Exception) {
                        changeMessage("Request failed: ${e.message}")
                    }
                }
            }
        ) {
            Text("Enter")
        }
    }
}