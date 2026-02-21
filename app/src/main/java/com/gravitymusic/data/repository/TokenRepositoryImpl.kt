package com.gravitymusic.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gravitymusic.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class TokenRepositoryImpl @Inject constructor(
    private val context: Context
) : TokenRepository {

    private val tokenKey = stringPreferencesKey("yandex_oauth_token")

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(tokenKey)
        }
    }

    override fun getToken(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[tokenKey]
        }
    }
}
