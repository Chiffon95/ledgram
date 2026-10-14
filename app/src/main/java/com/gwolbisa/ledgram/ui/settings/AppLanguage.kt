package com.gwolbisa.ledgram.ui.settings

internal enum class AppLanguage(val languageTag: String) {
    SYSTEM_DEFAULT(""),
    KOREAN("ko"),
    ENGLISH("en"),
    ;

    companion object {
        fun fromLanguageTags(languageTags: String): AppLanguage {
            val primaryLanguage = languageTags
                .substringBefore(',')
                .substringBefore('-')
                .lowercase()
            return entries.firstOrNull { it.languageTag == primaryLanguage } ?: SYSTEM_DEFAULT
        }
    }
}
