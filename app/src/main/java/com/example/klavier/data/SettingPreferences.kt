package com.example.klavier.data

// Enum des layouts
enum class Layout {
    US, FR
}

// Classe de données qui gère les préférences d'application
data class SettingPreferences(
    val isDarkTheme: Boolean,
    val layout: Layout,
    val sensibility: Float)
