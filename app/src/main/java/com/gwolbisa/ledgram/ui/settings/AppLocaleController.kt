package com.gwolbisa.ledgram.ui.settings

import android.app.Activity
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

internal class AppLocaleController(
    private val activity: Activity,
) {
    fun currentLanguage(): AppLanguage = AppLanguage.fromLanguageTags(currentLanguageTags())

    fun applyLanguage(language: AppLanguage): Boolean {
        if (language == currentLanguage()) return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(language.languageTag)
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.languageTag),
            )
        }
        return true
    }

    private fun currentLanguageTags(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.getSystemService(LocaleManager::class.java)
                .applicationLocales
                .toLanguageTags()
        } else {
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        }
}
