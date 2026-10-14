package com.gwolbisa.ledgram.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

internal class AppLocaleController {
    fun currentLanguage(): AppLanguage = AppLanguage.fromLanguageTags(currentLanguageTags())

    fun applyLanguage(language: AppLanguage): Boolean {
        if (language == currentLanguage()) return false

        val locales = if (language == AppLanguage.SYSTEM_DEFAULT) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(language.languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
        return true
    }

    private fun currentLanguageTags(): String =
        AppCompatDelegate.getApplicationLocales().toLanguageTags()
}
