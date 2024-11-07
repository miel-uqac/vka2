package com.example.klavier

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.klavier.data.Layout
import com.example.klavier.data.SettingPreferenceRepository
import kotlinx.coroutines.launch


class SettingModelView(settingPreferencesRepository: SettingPreferenceRepository,
                       private val sendData: (String) -> Unit
) : ViewModel() {

    private val Repository = settingPreferencesRepository
    val settingPreferences = Repository.SettingPreferencesFlow

    fun updateDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            Repository.updateDarkTheme(isDarkTheme)
        }

    }

    fun updateLayout(layout: Layout) {
        viewModelScope.launch {
            Repository.updateLayout(layout)
            sendData(R.string.id_layout.toString() + layout.ordinal.toString())
        }
    }
}