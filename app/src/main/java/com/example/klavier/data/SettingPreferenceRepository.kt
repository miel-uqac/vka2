package com.example.klavier.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Classe qui gère les interactions avec le datastore
class SettingPreferenceRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys{
        val DARK_THEME_KEY  = booleanPreferencesKey("dark_theme")
        val LAYOUT_KEY = stringPreferencesKey("layout")
        val SENSIBILITY_KEY = floatPreferencesKey("sensibility")
    }

    // Objet qui permet de lire le datastore
    val SettingPreferencesFlow: Flow<SettingPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val isDarkTheme = preferences[PreferencesKeys.DARK_THEME_KEY] ?: false
            val layout = preferences[PreferencesKeys.LAYOUT_KEY] ?: "FR"
            val sensibility = preferences[PreferencesKeys.SENSIBILITY_KEY] ?: 1.2f
            SettingPreferences(isDarkTheme , Layout.valueOf(layout), sensibility)
        }

    // fonction qui permet d'écrire dans le datastore (ici le thème)
    suspend fun updateDarkTheme(isDarkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_KEY] = isDarkTheme
        }
    }

    // fonction qui permet d'écrire dans le datastore (ici le layout)
    suspend fun updateLayout(layout: Layout) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAYOUT_KEY] = layout.toString()
        }
    }

    // fonction qui permet d'écrire dans le datastore (ici la sensibilité)
    suspend fun updateSensibility(sensibility: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SENSIBILITY_KEY] = sensibility
        }

    }
}