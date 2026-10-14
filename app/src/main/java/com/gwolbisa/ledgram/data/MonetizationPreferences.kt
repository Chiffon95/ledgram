package com.gwolbisa.ledgram.data

data class MonetizationPreferences(
    val displayExitCount: Int = 0,
    val lastInterstitialShownAtMillis: Long = 0L,
    val previousInterstitialShownAtMillis: Long = 0L,
)
