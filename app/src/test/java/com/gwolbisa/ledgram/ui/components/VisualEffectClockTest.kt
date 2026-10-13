package com.gwolbisa.ledgram.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualEffectClockTest {
    @Test
    fun `phase wrapping stays in normalized cycle`() {
        assertEquals(0.25, wrapVisualEffectPhase(1.25), 0.000001)
        assertEquals(0.75, wrapVisualEffectPhase(-0.25), 0.000001)
        assertEquals(0.0, wrapVisualEffectPhase(Double.NaN), 0.0)
    }

    @Test
    fun `blink keeps 650 to 350 visible hidden ratio`() {
        assertEquals(1f, blinkAlphaForPhase(0.0))
        assertEquals(1f, blinkAlphaForPhase(0.649))
        assertEquals(0f, blinkAlphaForPhase(0.65))
        assertEquals(0f, blinkAlphaForPhase(0.999))
        assertEquals(1f, blinkAlphaForPhase(1.0))
    }

    @Test
    fun `pulse reaches normal values at endpoints and minimum at midpoint`() {
        assertEquals(VisualTransform(), pulseTransformForPhase(0.0))
        assertEquals(VisualTransform(), pulseTransformForPhase(1.0))
        assertEquals(
            TextGlowEffectDefaults.PULSE_MIN_ALPHA,
            pulseTransformForPhase(0.5).alpha,
            0.0001f,
        )
        assertEquals(
            TextGlowEffectDefaults.PULSE_MIN_SCALE,
            pulseTransformForPhase(0.5).scale,
            0.0001f,
        )
    }

    @Test
    fun `fade is linear from zero to midpoint and back`() {
        assertEquals(0f, fadeAlphaForPhase(0.0), 0f)
        assertEquals(0.5f, fadeAlphaForPhase(0.25), 0.0001f)
        assertEquals(1f, fadeAlphaForPhase(0.5), 0f)
        assertEquals(0.5f, fadeAlphaForPhase(0.75), 0.0001f)
        assertEquals(0f, fadeAlphaForPhase(1.0), 0f)
    }

    @Test
    fun `speed changes phase increment rate without replacing current phase`() {
        val existingPhase = 0.4
        val slow = advanceVisualEffectPhase(existingPhase, 0.1, 0.25f, 1.0)
        val normal = advanceVisualEffectPhase(existingPhase, 0.1, 1f, 1.0)
        val fast = advanceVisualEffectPhase(existingPhase, 0.1, 5f, 1.0)

        assertEquals(0.425, slow, 0.000001)
        assertEquals(0.5, normal, 0.000001)
        assertEquals(0.9, fast, 0.000001)
        assertEquals(
            existingPhase,
            advanceVisualEffectPhase(existingPhase, 0.0, 5f, 1.0),
            0.0,
        )
        assertTrue(fast - existingPhase > normal - existingPhase)
        assertTrue(normal - existingPhase > slow - existingPhase)
    }
}
