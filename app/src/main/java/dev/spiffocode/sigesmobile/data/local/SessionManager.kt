package com.spiffocode.siges.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "siges_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN  = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val ROLE          = stringPreferencesKey("role")
    }

    val accessTokenFlow: Flow<String?> = context.dataStore.data
        .map { it[Keys.ACCESS_TOKEN] }

    val refreshTokenFlow: Flow<String?> = context.dataStore.data
        .map { it[Keys.REFRESH_TOKEN] }

    // Blocking accessors for use inside OkHttp interceptors (non-coroutine context)
    val accessToken: String?  get() = runBlocking { accessTokenFlow.first() }
    val refreshToken: String? get() = runBlocking { refreshTokenFlow.first() }
    val role: String?         get() = runBlocking { context.dataStore.data.map { it[Keys.ROLE] }.first() }
    val isLoggedIn: Boolean   get() = accessToken != null

    suspend fun saveSession(accessToken: String, refreshToken: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN]  = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.ROLE]          = role
        }
    }

    suspend fun updateAccessToken(token: String) {
        context.dataStore.edit { it[Keys.ACCESS_TOKEN] = token }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}