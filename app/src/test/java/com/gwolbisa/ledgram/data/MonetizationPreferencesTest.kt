package com.gwolbisa.ledgram.data

import androidx.datastore.preferences.core.mutablePreferencesOf
import org.junit.Assert.assertEquals
import org.junit.Test

class MonetizationPreferencesTest {
    @Test
    fun `monetization preferences default independently from editor state`() {
        assertEquals(MonetizationPreferences(), mutablePreferencesOf().toMonetizationPreferences())
    }

    @Test
    fun `monetization preferences persist counter and bounded timestamps`() {
        val expected = MonetizationPreferences(
            displayExitCount = 7,
            lastInterstitialShownAtMillis = 2_000L,
            previousInterstitialShownAtMillis = 1_000L,
        )
        val stored = mutablePreferencesOf().apply {
            putMonetizationPreferences(expected)
        }

        assertEquals(expected, stored.toMonetizationPreferences())
    }

    @Test
    fun `negative monetization values normalize safely`() {
        val stored = mutablePreferencesOf(
            MonetizationPreferenceKeys.displayExitCount to -5,
            MonetizationPreferenceKeys.lastInterstitialShownAtMillis to -2L,
            MonetizationPreferenceKeys.previousInterstitialShownAtMillis to -1L,
        )

        assertEquals(MonetizationPreferences(), stored.toMonetizationPreferences())
    }
}
