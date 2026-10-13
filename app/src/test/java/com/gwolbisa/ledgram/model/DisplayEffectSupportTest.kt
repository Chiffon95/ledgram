package com.gwolbisa.ledgram.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayEffectSupportTest {
    @Test
    fun `implemented text glow effects remain available at runtime`() {
        val implemented = listOf(
            DisplayEffect.STATIC,
            DisplayEffect.BLINK,
            DisplayEffect.PULSE,
            DisplayEffect.FADE,
        )

        implemented.forEach { effect ->
            assertTrue(effect.isTextGlowEffectImplemented())
            assertEquals(effect, effect.forCurrentRuntime())
        }
    }

    @Test
    fun `marquee effects remain unsupported and fall back to static`() {
        val unsupported = listOf(
            DisplayEffect.RIGHT_TO_LEFT,
            DisplayEffect.LEFT_TO_RIGHT,
        )

        unsupported.forEach { effect ->
            assertFalse(effect.isTextGlowEffectImplemented())
            assertEquals(DisplayEffect.STATIC, effect.forCurrentRuntime())
        }
    }
}
