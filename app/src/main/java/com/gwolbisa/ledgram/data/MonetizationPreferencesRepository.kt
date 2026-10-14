package com.gwolbisa.ledgram.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.gwolbisa.ledgram.ads.InterstitialEvent
import com.gwolbisa.ledgram.ads.applyInterstitialEvent
import com.gwolbisa.ledgram.ads.recordCompletedDisplaySession
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal object MonetizationPreferenceKeys {
    val displayExitCount = intPreferencesKey("display_exit_count")
    val lastInterstitialShownAtMillis = longPreferencesKey("last_interstitial_shown_at_millis")
    val previousInterstitialShownAtMillis =
        longPreferencesKey("previous_interstitial_shown_at_millis")
}

class MonetizationPreferencesRepository internal constructor(
    private val dataStore: DataStore<Preferences>,
) {
    constructor(context: Context) : this(context.applicationContext.editorPreferencesDataStore)

    val preferences: Flow<MonetizationPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map(Preferences::toMonetizationPreferences)

    suspend fun recordDisplayCompletion(): MonetizationPreferences = update { current ->
        recordCompletedDisplaySession(current)
    }

    suspend fun recordInterstitialShown(nowMillis: Long): MonetizationPreferences =
        update { current ->
            applyInterstitialEvent(current, InterstitialEvent.SHOWN, nowMillis)
        }

    private suspend fun update(
        transform: (MonetizationPreferences) -> MonetizationPreferences,
    ): MonetizationPreferences {
        var updated = MonetizationPreferences()
        dataStore.edit { stored ->
            updated = transform(stored.toMonetizationPreferences())
            stored.putMonetizationPreferences(updated)
        }
        return updated
    }
}

internal fun MutablePreferences.putMonetizationPreferences(
    preferences: MonetizationPreferences,
) {
    this[MonetizationPreferenceKeys.displayExitCount] = preferences.displayExitCount
    this[MonetizationPreferenceKeys.lastInterstitialShownAtMillis] =
        preferences.lastInterstitialShownAtMillis
    this[MonetizationPreferenceKeys.previousInterstitialShownAtMillis] =
        preferences.previousInterstitialShownAtMillis
}

internal fun Preferences.toMonetizationPreferences(): MonetizationPreferences =
    MonetizationPreferences(
        displayExitCount = (this[MonetizationPreferenceKeys.displayExitCount] ?: 0)
            .coerceAtLeast(0),
        lastInterstitialShownAtMillis =
            (this[MonetizationPreferenceKeys.lastInterstitialShownAtMillis] ?: 0L)
                .coerceAtLeast(0L),
        previousInterstitialShownAtMillis =
            (this[MonetizationPreferenceKeys.previousInterstitialShownAtMillis] ?: 0L)
                .coerceAtLeast(0L),
    )
