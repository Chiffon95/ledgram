package com.gwolbisa.ledgram.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationSpeedTest {
    @Test
    fun `effect duration supports full range`() {
        assertEquals(4_000, scaledEffectDurationMillis(1_000, 0.25f))
        assertEquals(1_000, scaledEffectDurationMillis(1_000, 1f))
        assertEquals(200, scaledEffectDurationMillis(1_000, 5f))
    }

    @Test
    fun `marquee speed supports full range`() {
        assertEquals(22.5f, scaledMarqueeSpeed(90f, 0.25f))
        assertEquals(90f, scaledMarqueeSpeed(90f, 1f))
        assertEquals(450f, scaledMarqueeSpeed(90f, 5f))
    }

    @Test
    fun `invalid and out of range multipliers are handled safely`() {
        assertEquals(DEFAULT_SPEED_MULTIPLIER, normalizeMotionSpeedMultiplier(Float.NaN))
        assertEquals(DEFAULT_SPEED_MULTIPLIER, normalizeEffectSpeedMultiplier(Float.POSITIVE_INFINITY))
        assertEquals(DEFAULT_SPEED_MULTIPLIER, normalizeEffectSpeedMultiplier(Float.NEGATIVE_INFINITY))
        assertEquals(MIN_SPEED_MULTIPLIER, normalizeMotionSpeedMultiplier(0f))
        assertEquals(MAX_SPEED_MULTIPLIER, normalizeEffectSpeedMultiplier(6f))
    }

    @Test
    fun `supported speed choices include exact bounds and default`() {
        assertEquals(MIN_SPEED_MULTIPLIER, SUPPORTED_SPEED_MULTIPLIERS.first())
        assertEquals(MAX_SPEED_MULTIPLIER, SUPPORTED_SPEED_MULTIPLIERS.last())
        assertTrue(SUPPORTED_SPEED_MULTIPLIERS.contains(DEFAULT_SPEED_MULTIPLIER))
    }

    @Test
    fun `motion and effect calculations use only their supplied multiplier`() {
        val marqueeSpeed = scaledMarqueeSpeed(90f, 5f)
        val effectDuration = scaledEffectDurationMillis(1_000, 0.25f)

        assertEquals(450f, marqueeSpeed)
        assertEquals(4_000, effectDuration)
    }

    @Test
    fun `duration and speed remain safe for invalid bases`() {
        assertEquals(1, scaledEffectDurationMillis(0, 5f))
        assertEquals(1, scaledEffectDurationMillis(-1_000, 0.25f))
        assertEquals(0f, scaledMarqueeSpeed(Float.NaN, 1f))
        assertEquals(0f, scaledMarqueeSpeed(-90f, 1f))
    }
}
