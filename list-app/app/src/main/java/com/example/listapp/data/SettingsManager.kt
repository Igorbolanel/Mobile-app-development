package com.example.listapp.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class SettingsManager(context: Context) {

    private val preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun getLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, "") ?: ""
    }

    fun saveLanguage(language: String) {
        preferences.edit {
            putString(KEY_LANGUAGE, language)
        }
    }

    fun getTheme(): Int {
        return preferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun saveTheme(theme: Int) {
        preferences.edit {
            putInt(KEY_THEME, theme)
        }
    }

    companion object {
        private const val KEY_LANGUAGE = "language"
        private const val KEY_THEME = "theme"

        val LANGUAGES = arrayOf("", "ru", "en")
        val THEMES = arrayOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            AppCompatDelegate.MODE_NIGHT_NO,
            AppCompatDelegate.MODE_NIGHT_YES
        )
    }
}
