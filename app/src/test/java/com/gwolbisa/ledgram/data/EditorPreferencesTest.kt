package com.gwolbisa.ledgram.data

import androidx.datastore.preferences.core.mutablePreferencesOf
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.editor.EditorUiState
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorPreferencesTest {
    @Test
    fun `default preferences map to default editor state`() {
        assertEquals(EditorUiState(), EditorPreferences().toEditorUiState())
        assertEquals(EditorPreferences(), mutablePreferencesOf().toEditorPreferences())
    }

    @Test
    fun `editor preferences round trip every persisted field`() {
        val state = EditorUiState(
            message = "HELLO\nWORLD\nLEDGRAM",
            textColor = 0xFF123456,
            backgroundColor = 0xFF654321,
            bold = true,
            italic = true,
            underline = true,
            renderStyle = RenderStyle.DOT_MATRIX,
            motionEffect = MotionEffect.RIGHT_TO_LEFT,
            visualEffect = VisualEffect.PULSE,
            motionSpeedMultiplier = 5f,
            effectSpeedMultiplier = 0.5f,
        )

        val stored = mutablePreferencesOf().apply {
            putEditorPreferences(state.toEditorPreferences())
        }
        val restored = stored.toEditorPreferences().toEditorUiState()

        assertEquals(state, restored)
        assertEquals("HELLO\nWORLD\nLEDGRAM", restored.message)
    }

    @Test
    fun `invalid persisted values fall back or normalize safely`() {
        val stored = mutablePreferencesOf(
            EditorPreferenceKeys.message to "A\r\nB\rC",
            EditorPreferenceKeys.textColor to -1L,
            EditorPreferenceKeys.backgroundColor to Long.MAX_VALUE,
            EditorPreferenceKeys.bold to true,
            EditorPreferenceKeys.renderStyle to "UNKNOWN_RENDERER",
            EditorPreferenceKeys.motionEffect to "SIDEWAYS",
            EditorPreferenceKeys.visualEffect to "SPARKLE",
            EditorPreferenceKeys.motionSpeedMultiplier to Float.NaN,
            EditorPreferenceKeys.effectSpeedMultiplier to 100f,
        )

        val preferences = stored.toEditorPreferences()

        assertEquals("A\nB\nC", preferences.message)
        assertEquals(BrandColorValue.TEXT, preferences.textColor)
        assertEquals(BrandColorValue.BACKGROUND, preferences.backgroundColor)
        assertEquals(RenderStyle.TEXT_GLOW, preferences.renderStyle)
        assertEquals(MotionEffect.STATIC, preferences.motionEffect)
        assertEquals(VisualEffect.NONE, preferences.visualEffect)
        assertEquals(1f, preferences.motionSpeedMultiplier)
        assertEquals(5f, preferences.effectSpeedMultiplier)
        assertTrue(preferences.bold)
        assertFalse(preferences.italic)
    }
}
