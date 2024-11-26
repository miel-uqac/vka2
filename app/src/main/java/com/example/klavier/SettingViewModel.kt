package com.example.klavier

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klavier.data.Layout
import com.example.klavier.data.SettingPreferenceRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val settingPreferencesRepository: SettingPreferenceRepository,
                       private val sendData: (String) -> Unit
                       , private val context: Context
) : ViewModel() {

    val settingPreferences = settingPreferencesRepository.SettingPreferencesFlow

    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            settingPreferencesRepository.updateDarkTheme(isDarkTheme)
        }

    }

    fun updateLayout(layout: Layout) {
        viewModelScope.launch {
            settingPreferencesRepository.updateLayout(layout)
            sendData(context.getString(R.string.id_layout) + layout.ordinal.toString())
        }
    }

    fun updateSensibility(sensibility: Float) {
        viewModelScope.launch {
            settingPreferencesRepository.updateSensibility(sensibility)
        }
    }
}