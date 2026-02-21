package com.gravitymusic.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeConfig {
    SYSTEM, LIGHT, DARK
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val themeKey = stringPreferencesKey("theme_config")

    val themeConfig: Flow<ThemeConfig> = context.dataStore.data.map { prefs ->
        when (prefs[themeKey]) {
            ThemeConfig.LIGHT.name -> ThemeConfig.LIGHT
            ThemeConfig.DARK.name -> ThemeConfig.DARK
            else -> ThemeConfig.SYSTEM
        }
    }

    suspend fun setThemeConfig(config: ThemeConfig) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = config.name
        }
    }
}
