package com.example.listapp

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.listapp.data.SettingsManager

class ListApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val settingsManager = SettingsManager(this)
        AppCompatDelegate.setDefaultNightMode(settingsManager.getTheme())
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(settingsManager.getLanguage())
            )
        }
    }
}
