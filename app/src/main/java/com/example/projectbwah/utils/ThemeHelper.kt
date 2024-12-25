package com.example.projectbwah.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

class ThemeHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("theme_prefs", MODE_PRIVATE)

    fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("is_dark_theme", false) // Default to light theme
    }

    fun setDarkTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }
}