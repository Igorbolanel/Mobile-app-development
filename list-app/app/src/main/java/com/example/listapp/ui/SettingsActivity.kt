package com.example.listapp.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listapp.R
import com.example.listapp.data.SettingsManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var languageValue: TextView
    private lateinit var themeValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsManager = SettingsManager(this)

        languageValue = findViewById(R.id.languageValue)
        themeValue = findViewById(R.id.themeValue)
        val backButton = findViewById<ImageButton>(R.id.backButton)
        val languageRow = findViewById<LinearLayout>(R.id.languageRow)
        val themeRow = findViewById<LinearLayout>(R.id.themeRow)

        showValues()

        backButton.setOnClickListener { finish() }
        languageRow.setOnClickListener { showLanguageDialog() }
        themeRow.setOnClickListener { showThemeDialog() }
    }

    private fun showValues() {
        languageValue.text = getLanguageNames()[getLanguageIndex()]
        themeValue.text = getThemeNames()[getThemeIndex()]
    }

    private fun getLanguageNames(): Array<String> {
        return arrayOf(
            getString(R.string.system_default),
            getString(R.string.language_russian),
            getString(R.string.language_english)
        )
    }

    private fun getThemeNames(): Array<String> {
        return arrayOf(
            getString(R.string.system_default),
            getString(R.string.theme_light),
            getString(R.string.theme_dark)
        )
    }

    private fun getLanguageIndex(): Int {
        val index = SettingsManager.LANGUAGES.indexOf(settingsManager.getLanguage())
        return if (index == -1) 0 else index
    }

    private fun getThemeIndex(): Int {
        val index = SettingsManager.THEMES.indexOf(settingsManager.getTheme())
        return if (index == -1) 0 else index
    }

    private fun showLanguageDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.language)
            .setSingleChoiceItems(getLanguageNames(), getLanguageIndex()) { dialog, which ->
                dialog.dismiss()
                val language = SettingsManager.LANGUAGES[which]
                settingsManager.saveLanguage(language)
                showValues()
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
            }
            .show()
    }

    private fun showThemeDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.theme)
            .setSingleChoiceItems(getThemeNames(), getThemeIndex()) { dialog, which ->
                dialog.dismiss()
                val theme = SettingsManager.THEMES[which]
                settingsManager.saveTheme(theme)
                showValues()
                AppCompatDelegate.setDefaultNightMode(theme)
            }
            .show()
    }
}
