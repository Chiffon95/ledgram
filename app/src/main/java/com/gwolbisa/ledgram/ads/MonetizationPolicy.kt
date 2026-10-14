package com.gwolbisa.ledgram.ads

import com.gwolbisa.ledgram.data.MonetizationPreferences

internal const val DISPLAY_EXITS_PER_INTERSTITIAL = 5
internal const val INTERSTITIAL_FREQUENCY_WINDOW_MILLIS = 30L * 60L * 1_000L
internal const val MAX_INTERSTITIALS_PER_WINDOW = 2

internal enum class InterstitialEvent {
    SHOWN,
    LOAD_FAILED,
    SHOW_FAILED,
    DISMISSED,
}

internal fun recordCompletedDisplaySession(
    current: MonetizationPreferences,
): MonetizationPreferences = current.copy(
    displayExitCount = if (current.displayExitCount == Int.MAX_VALUE) {
        Int.MAX_VALUE
    } else {
        current.displayExitCount + 1
    },
)

internal fun isInterstitialEligible(
    preferences: MonetizationPreferences,
    nowMillis: Long,
): Boolean {
    if (preferences.displayExitCount < DISPLAY_EXITS_PER_INTERSTITIAL) return false
    val recentShowCount = listOf(
        preferences.lastInterstitialShownAtMillis,
        preferences.previousInterstitialShownAtMillis,
    ).count { shownAtMillis ->
        shownAtMillis > 0L && nowMillis - shownAtMillis < INTERSTITIAL_FREQUENCY_WINDOW_MILLIS
    }
    return recentShowCount < MAX_INTERSTITIALS_PER_WINDOW
}

internal fun applyInterstitialEvent(
    current: MonetizationPreferences,
    event: InterstitialEvent,
    nowMillis: Long,
): MonetizationPreferences = when (event) {
    InterstitialEvent.SHOWN -> current.copy(
        displayExitCount = 0,
        lastInterstitialShownAtMillis = nowMillis.coerceAtLeast(0L),
        previousInterstitialShownAtMillis = current.lastInterstitialShownAtMillis,
    )

    InterstitialEvent.LOAD_FAILED,
    InterstitialEvent.SHOW_FAILED,
    InterstitialEvent.DISMISSED,
    -> current
}
