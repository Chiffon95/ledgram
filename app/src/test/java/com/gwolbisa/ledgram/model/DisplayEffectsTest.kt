package com.gwolbisa.ledgram.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayEffectsTest {
    @Test
    fun `motion effects contain every supported motion`() {
        assertEquals(
            listOf(
                MotionEffect.STATIC,
                MotionEffect.RIGHT_TO_LEFT,
                MotionEffect.LEFT_TO_RIGHT,
            ),
            MotionEffect.entries,
        )
    }

    @Test
    fun `visual effects contain every supported visual animation`() {
        assertEquals(
            listOf(
                VisualEffect.NONE,
                VisualEffect.BLINK,
                VisualEffect.PULSE,
                VisualEffect.FADE,
            ),
            VisualEffect.entries,
        )
    }
}
