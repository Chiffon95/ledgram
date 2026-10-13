package com.gwolbisa.ledgram.ui.editor

import com.gwolbisa.ledgram.model.DisplayEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorUiStateTest {
    @Test
    fun `display config contains normalized supported editor selections`() {
        val state = EditorUiState(
            message = "  Hello\nworld  ",
            textColor = 0xFFAABBCC,
            backgroundColor = 0xFF112233,
            bold = true,
            italic = true,
            underline = true,
            renderStyle = RenderStyle.DOT_MATRIX,
            effect = DisplayEffect.PULSE,
        )

        val config = state.toDisplayConfig()

        assertEquals("Hello world", config.message)
        assertEquals(0xFFAABBCC, config.textColor)
        assertEquals(0xFF112233, config.backgroundColor)
        assertTrue(config.bold)
        assertTrue(config.italic)
        assertTrue(config.underline)
        assertEquals(RenderStyle.DOT_MATRIX, config.renderStyle)
        assertEquals(DisplayEffect.PULSE, state.effect)
        assertEquals(DisplayEffect.PULSE, state.runtimeEffect)
        assertEquals(DisplayEffect.PULSE, config.effect)
    }

    @Test
    fun `defaults use white text on black background`() {
        val state = EditorUiState()

        assertEquals(BrandColorValue.TEXT, state.textColor)
        assertEquals(BrandColorValue.BACKGROUND, state.backgroundColor)
    }

    @Test
    fun `swap colors exchanges text and background values`() {
        val state = EditorUiState(
            textColor = 0xFFFF3B30,
            backgroundColor = 0xFF007AFF,
        )

        val swapped = state.swapColors()

        assertEquals(0xFF007AFF, swapped.textColor)
        assertEquals(0xFFFF3B30, swapped.backgroundColor)
        assertEquals(0xFF007AFF, swapped.toDisplayConfig().textColor)
        assertEquals(0xFFFF3B30, swapped.toDisplayConfig().backgroundColor)
    }

    @Test
    fun `start availability ignores surrounding whitespace`() {
        assertFalse(EditorUiState(message = " \t ").canStartDisplay)
        assertTrue(EditorUiState(message = "  LEDgram  ").canStartDisplay)
    }
}
