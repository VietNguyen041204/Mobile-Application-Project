package com.myapp.greetingcard

//import android.content.Context
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
import kotlinx.serialization.Serializable

@Serializable
data class UserCredential(val email: String)

@Serializable
data class TokenResponse(
    val code: Int,
    val message: String
)

@Serializable
data class AudioRequest(
    val word: String,
    val email: String,
    val token: String
)

@Serializable
data class AudioResponse(
    val code: Int,
    val message: String
)
