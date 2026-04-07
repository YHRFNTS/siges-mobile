package dev.spiffocode.sigesmobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
        val ACCESS_TOKEN        = stringPreferencesKey("access_token")
        val REFRESH_TOKEN       = stringPreferencesKey("refresh_token")
        val ROLE                = stringPreferencesKey("role")
        val FIRST_NAME          = stringPreferencesKey("first_name")
        val LAST_NAME           = stringPreferencesKey("last_name")
        val EMAIL               = stringPreferencesKey("email")
        val EMPLOYEE_NUMBER     = stringPreferencesKey("employee_number")
        val REGISTRATION_NUMBER = stringPreferencesKey("registration_number")
        val PROFILE_PICTURE_URL = stringPreferencesKey("profile_picture_url")
        val PHONE_NUMBER        = stringPreferencesKey("phone_number")
        val BIRTH_DATE          = stringPreferencesKey("birth_date")
        val ID                  = stringPreferencesKey("user_id")
        val REMEMBER_ME         = booleanPreferencesKey("remember_me")
        val FCM_TOKEN           = stringPreferencesKey("fcm_token")
    }

    val accessTokenFlow: Flow<String?>  = context.dataStore.data.map { it[Keys.ACCESS_TOKEN] }
    val refreshTokenFlow: Flow<String?> = context.dataStore.data.map { it[Keys.REFRESH_TOKEN] }

    val accessToken: String?        get() = runBlocking { accessTokenFlow.first() }
    val refreshToken: String?       get() = runBlocking { refreshTokenFlow.first() }
    val role: String?               get() = runBlocking { context.dataStore.data.map { it[Keys.ROLE] }.first() }
    val firstName: String?          get() = runBlocking { context.dataStore.data.map { it[Keys.FIRST_NAME] }.first() }
    val lastName: String?           get() = runBlocking { context.dataStore.data.map { it[Keys.LAST_NAME] }.first() }
    val email: String?              get() = runBlocking { context.dataStore.data.map { it[Keys.EMAIL] }.first() }
    val employeeNumber: String?     get() = runBlocking { context.dataStore.data.map { it[Keys.EMPLOYEE_NUMBER] }.first() }
    val registrationNumber: String? get() = runBlocking { context.dataStore.data.map { it[Keys.REGISTRATION_NUMBER] }.first() }
    val profilePictureUrl: String?  get() = runBlocking { context.dataStore.data.map { it[Keys.PROFILE_PICTURE_URL] }.first() }

    val phoneNumber: String?        get() = runBlocking { context.dataStore.data.map { it[Keys.PHONE_NUMBER] }.first() }
    val birthDate: String?          get() = runBlocking { context.dataStore.data.map { it[Keys.BIRTH_DATE] }.first() }
    val id: String?                 get() = runBlocking { context.dataStore.data.map { it[Keys.ID] }.first() }
    val rememberMe: Boolean         get() = runBlocking { context.dataStore.data.map { it[Keys.REMEMBER_ME] ?: false }.first() }
    val fcmToken: String?           get() = runBlocking { context.dataStore.data.map { it[Keys.FCM_TOKEN] }.first() }



    val isLoggedIn: Boolean get() = accessToken != null

    suspend fun saveSession(
        id: String,
        accessToken: String,
        refreshToken: String,
        role: String,
        firstName: String,
        lastName: String,
        email: String,
        birthDate: String,
        employeeNumber: String? = null,
        registrationNumber: String? = null,
        profilePictureUrl: String? = null,
        phoneNumber: String,
        rememberMe: Boolean = false
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REMEMBER_ME]   = rememberMe
            prefs[Keys.ID]            = id
            prefs[Keys.ACCESS_TOKEN]  = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
            prefs[Keys.ROLE]          = role
            prefs[Keys.FIRST_NAME]    = firstName
            prefs[Keys.LAST_NAME]     = lastName
            prefs[Keys.EMAIL]         = email
            prefs[Keys.PHONE_NUMBER]  = phoneNumber
            prefs[Keys.BIRTH_DATE]    = birthDate
            if (employeeNumber != null)     prefs[Keys.EMPLOYEE_NUMBER]     = employeeNumber
            if (registrationNumber != null) prefs[Keys.REGISTRATION_NUMBER] = registrationNumber
            if (profilePictureUrl != null)  prefs[Keys.PROFILE_PICTURE_URL] = profilePictureUrl
        }
    }

    suspend fun updateAccessToken(token: String) {
        context.dataStore.edit { it[Keys.ACCESS_TOKEN] = token }
    }

    suspend fun updateTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit {
            it[Keys.ACCESS_TOKEN] = accessToken
            it[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateProfilePictureUrl(url: String) {
        context.dataStore.edit { it[Keys.PROFILE_PICTURE_URL] = url }
    }

    suspend fun saveFcmToken(token: String) {
        context.dataStore.edit { it[Keys.FCM_TOKEN] = token }
    }
 
    suspend fun updateUserInfo(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        birthDate: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.FIRST_NAME]   = firstName
            prefs[Keys.LAST_NAME]    = lastName
            prefs[Keys.EMAIL]        = email
            prefs[Keys.PHONE_NUMBER] = phoneNumber
            prefs[Keys.BIRTH_DATE]   = birthDate
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            val remember = prefs[Keys.REMEMBER_ME] ?: false
            val fcmToken = prefs[Keys.FCM_TOKEN] ?: ""
            prefs.clear()
            prefs[Keys.REMEMBER_ME] = remember
            prefs[Keys.FCM_TOKEN] = fcmToken
        }
    }
}