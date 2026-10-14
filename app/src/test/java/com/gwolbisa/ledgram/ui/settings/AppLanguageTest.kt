package com.gwolbisa.ledgram.ui.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun `system default maps to an empty language tag`() {
        assertEquals("", AppLanguage.SYSTEM_DEFAULT.languageTag)
        assertEquals(AppLanguage.SYSTEM_DEFAULT, AppLanguage.fromLanguageTags(""))
    }

    @Test
    fun `Korean maps to ko`() {
        assertEquals("ko", AppLanguage.KOREAN.languageTag)
        assertEquals(AppLanguage.KOREAN, AppLanguage.fromLanguageTags("ko"))
    }

    @Test
    fun `English maps to en`() {
        assertEquals("en", AppLanguage.ENGLISH.languageTag)
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromLanguageTags("en"))
    }

    @Test
    fun `regional locale resolves by primary language`() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromLanguageTags("en-US"))
        assertEquals(AppLanguage.KOREAN, AppLanguage.fromLanguageTags("ko-KR"))
    }
}
