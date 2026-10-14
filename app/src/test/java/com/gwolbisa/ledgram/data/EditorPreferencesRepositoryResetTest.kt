package com.gwolbisa.ledgram.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class EditorPreferencesRepositoryResetTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `reset removes editor values and preserves monetization state`() = runBlocking {
        val dataStoreScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val dataStore = PreferenceDataStoreFactory.create(
            scope = dataStoreScope,
            produceFile = {
                File(temporaryFolder.root, "editor.preferences_pb")
            },
        )
        try {
            val customEditorPreferences = EditorPreferences(
                message = "CUSTOM",
                textColor = 0xFF123456,
                backgroundColor = 0xFF654321,
                bold = true,
                italic = true,
                underline = true,
                renderStyle = RenderStyle.DOT_MATRIX,
                motionEffect = MotionEffect.LEFT_TO_RIGHT,
                visualEffect = VisualEffect.FADE,
                motionSpeedMultiplier = 5f,
                effectSpeedMultiplier = 0.25f,
            )
            val monetizationPreferences = MonetizationPreferences(
                displayExitCount = 4,
                lastInterstitialShownAtMillis = 2_000L,
                previousInterstitialShownAtMillis = 1_000L,
            )
            dataStore.edit { stored ->
                stored.putEditorPreferences(customEditorPreferences)
                stored.putMonetizationPreferences(monetizationPreferences)
            }

            EditorPreferencesRepository(dataStore).reset()

            val stored = dataStore.data.first()
            assertEquals(EditorPreferences(), stored.toEditorPreferences())
            assertEquals(monetizationPreferences, stored.toMonetizationPreferences())
            assertNull(stored[EditorPreferenceKeys.message])
            assertNull(stored[EditorPreferenceKeys.motionSpeedMultiplier])
            assertNull(stored[EditorPreferenceKeys.effectSpeedMultiplier])
        } finally {
            dataStoreScope.cancel()
        }
    }
}
