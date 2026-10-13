package com.gwolbisa.ledgram.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayEffectSupportTest {
    @Test
    fun `implemented text glow effects remain available at runtime`() {
        val implemented = DisplayEffect.entries

        implemented.forEach { effect ->
            assertTrue(effect.isTextGlowEffectImplemented())
            assertEquals(effect, effect.forCurrentRuntime())
        }
    }

}
