package com.example.klavier

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klavier.data.Layout
import com.example.klavier.data.SettingPreferenceRepository
import kotlinx.coroutines.launch


// ViewModel pour la gestion des préférences d'application, permet de mettre à jour les préférences et de les lire
class SettingViewModel(private val settingPreferencesRepository: SettingPreferenceRepository,
                       private val sendData: (String) -> Unit
                       , private val context: Context
) : ViewModel() {

    val settingPreferences = settingPreferencesRepository.SettingPreferencesFlow

    //met à jour la préférence du thème
    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            settingPreferencesRepository.updateDarkTheme(isDarkTheme)
        }

    }

    //met à jour la préférence du layout
    fun updateLayout(layout: Layout) {
        viewModelScope.launch {
            settingPreferencesRepository.updateLayout(layout)
            sendData(context.getString(R.string.id_layout) + layout.ordinal.toString())
        }
    }

    //met à jour la préférence de la sensibilité
    fun updateSensibility(sensibility: Float) {
        viewModelScope.launch {
            settingPreferencesRepository.updateSensibility(sensibility)
        }
    }
}