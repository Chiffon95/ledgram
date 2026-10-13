package com.gwolbisa.ledgram.util

import org.junit.Assert.assertEquals
import org.junit.Test

class AnimationSpeedTest {
    @Test
    fun `half speed doubles duration`() {
        assertEquals(2_000, scaledAnimationDurationMillis(1_000, 0.5f))
    }

    @Test
    fun `double speed halves duration`() {
        assertEquals(500, scaledAnimationDurationMillis(1_000, 2f))
    }

    @Test
    fun `marquee speed scales proportionally`() {
        assertEquals(45f, scaledAnimationSpeed(90f, 0.5f))
        assertEquals(180f, scaledAnimationSpeed(90f, 2f))
    }

    @Test
    fun `invalid and out of range multipliers are handled safely`() {
        assertEquals(DEFAULT_SPEED_MULTIPLIER, normalizeSpeedMultiplier(Float.NaN))
        assertEquals(DEFAULT_SPEED_MULTIPLIER, normalizeSpeedMultiplier(Float.POSITIVE_INFINITY))
        assertEquals(MIN_SPEED_MULTIPLIER, normalizeSpeedMultiplier(0f))
        assertEquals(MAX_SPEED_MULTIPLIER, normalizeSpeedMultiplier(3f))
    }
}
