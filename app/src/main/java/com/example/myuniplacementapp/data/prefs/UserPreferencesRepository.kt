package com.example.myuniplacementapp.data.prefs


import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val THEME_KEY = booleanPreferencesKey("dark_theme_enabled")

class UserPreferencesRepository(private val dataStore: androidx.datastore.core.DataStore<Preferences>) {

    val isDarkTheme: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: false
    }

    suspend fun toggleTheme() {
        dataStore.edit { prefs ->
            val current = prefs[THEME_KEY] ?: false
            prefs[THEME_KEY] = !current
        }
    }
}
