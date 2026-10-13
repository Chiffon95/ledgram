package com.gwolbisa.ledgram.ui.editor

import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.util.DEFAULT_SPEED_MULTIPLIER
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
            motionEffect = MotionEffect.RIGHT_TO_LEFT,
            visualEffect = VisualEffect.PULSE,
            motionSpeedMultiplier = 2.5f,
            effectSpeedMultiplier = 0.75f,
        )

        val config = state.toDisplayConfig()

        assertEquals("Hello\nworld", config.message)
        assertEquals(0xFFAABBCC, config.textColor)
        assertEquals(0xFF112233, config.backgroundColor)
        assertTrue(config.bold)
        assertTrue(config.italic)
        assertTrue(config.underline)
        assertEquals(RenderStyle.DOT_MATRIX, config.renderStyle)
        assertEquals(MotionEffect.RIGHT_TO_LEFT, state.motionEffect)
        assertEquals(MotionEffect.RIGHT_TO_LEFT, config.motionEffect)
        assertEquals(VisualEffect.PULSE, state.visualEffect)
        assertEquals(VisualEffect.PULSE, config.visualEffect)
        assertEquals(2.5f, config.motionSpeedMultiplier)
        assertEquals(0.75f, config.effectSpeedMultiplier)
    }

    @Test
    fun `defaults use white text on black background`() {
        val state = EditorUiState()

        assertEquals(BrandColorValue.TEXT, state.textColor)
        assertEquals(BrandColorValue.BACKGROUND, state.backgroundColor)
        assertEquals(DEFAULT_SPEED_MULTIPLIER, state.motionSpeedMultiplier)
        assertEquals(DEFAULT_SPEED_MULTIPLIER, state.effectSpeedMultiplier)
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
        assertFalse(EditorUiState(message = "\n").canStartDisplay)
        assertFalse(EditorUiState(message = " \n ").canStartDisplay)
        assertTrue(EditorUiState(message = "  LEDgram  ").canStartDisplay)
    }

    @Test
    fun `empty message remains empty and cannot start display`() {
        val state = EditorUiState()

        assertEquals("", state.message)
        assertEquals("", state.toDisplayConfig().message)
        assertFalse(state.canStartDisplay)
    }

    @Test
    fun `empty message uses sample only in preview config`() {
        val state = EditorUiState()

        val previewConfig = state.toPreviewConfig("Hello!")
        val displayConfig = state.toDisplayConfig()

        assertEquals("Hello!", previewConfig.message)
        assertEquals("", state.message)
        assertEquals("", displayConfig.message)
        assertFalse(state.canStartDisplay)
    }

    @Test
    fun `whitespace message uses sample only in preview config`() {
        val state = EditorUiState(message = " \t ")

        val previewConfig = state.toPreviewConfig("Hello!")
        val displayConfig = state.toDisplayConfig()

        assertEquals("Hello!", previewConfig.message)
        assertEquals(" \t ", state.message)
        assertEquals("", displayConfig.message)
        assertFalse(state.canStartDisplay)
    }

    @Test
    fun `newline only message uses sample only in preview config`() {
        val state = EditorUiState().withMessage(" \n ")

        assertEquals(" \n ", state.message)
        assertEquals("Hello!", state.toPreviewConfig("Hello!").message)
        assertEquals("", state.toDisplayConfig().message)
        assertFalse(state.canStartDisplay)
    }

    @Test
    fun `display config normalizes speed multipliers independently`() {
        val config = EditorUiState(
            motionSpeedMultiplier = 10f,
            effectSpeedMultiplier = Float.NaN,
        ).toDisplayConfig()

        assertEquals(5f, config.motionSpeedMultiplier)
        assertEquals(1f, config.effectSpeedMultiplier)
    }

    @Test
    fun `every motion preserves stored multiline display content`() {
        val storedMessage = "HELLO\nWORLD\nLEDGRAM"
        val staticState = EditorUiState().withMessage(storedMessage)
        val rightToLeftState = staticState.copy(motionEffect = MotionEffect.RIGHT_TO_LEFT)
        val leftToRightState = staticState.copy(motionEffect = MotionEffect.LEFT_TO_RIGHT)
        val restoredStaticState = rightToLeftState.copy(motionEffect = MotionEffect.STATIC)

        assertEquals(storedMessage, staticState.message)
        assertEquals(storedMessage, staticState.toDisplayConfig().message)
        assertEquals(storedMessage, rightToLeftState.message)
        assertEquals(storedMessage, rightToLeftState.toDisplayConfig().message)
        assertEquals(storedMessage, leftToRightState.toDisplayConfig().message)
        assertEquals(storedMessage, restoredStaticState.message)
        assertEquals(storedMessage, restoredStaticState.toDisplayConfig().message)
    }

    @Test
    fun `every motion and visual effect combination maps to display config`() {
        MotionEffect.entries.forEach { motion ->
            VisualEffect.entries.forEach { visual ->
                val config = EditorUiState(
                    message = "LEDgram",
                    motionEffect = motion,
                    visualEffect = visual,
                ).toDisplayConfig()

                assertEquals(motion, config.motionEffect)
                assertEquals(visual, config.visualEffect)
            }
        }
    }
}
