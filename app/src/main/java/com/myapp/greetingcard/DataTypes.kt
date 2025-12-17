package com.myapp.greetingcard

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.serialization.Serializable

// --- Response Models ---
@Serializable
data class UserCredential(val email: String)

@Serializable
data class TokenResponse(
    val code: Int,
    val message: String
)


