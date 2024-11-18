package com.example.klavier.data

enum class Layout {
    US, FR
}

data class SettingPreferences(val isDarkTheme: Boolean,val layout: Layout)
