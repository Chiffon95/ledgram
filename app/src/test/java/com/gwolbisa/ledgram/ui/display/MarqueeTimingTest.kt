package com.gwolbisa.ledgram.ui.display

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarqueeTimingTest {
    @Test
    fun `larger travel distance produces longer duration`() {
        val shortDuration = calculateMarqueeDurationMillis(
            travelDistancePx = 1_000f,
            speedPxPerSecond = 200f,
        )
        val longDuration = calculateMarqueeDurationMillis(
            travelDistancePx = 2_000f,
            speedPxPerSecond = 200f,
        )

        assertTrue(longDuration > shortDuration)
    }

    @Test
    fun `zero distance and invalid speed use minimum duration`() {
        assertEquals(
            TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS,
            calculateMarqueeDurationMillis(0f, 200f),
        )
        assertEquals(
            TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS,
            calculateMarqueeDurationMillis(1_000f, 0f),
        )
    }

    @Test
    fun `duration never drops below minimum`() {
        assertEquals(
            TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS,
            calculateMarqueeDurationMillis(1f, 10_000f),
        )
    }
}
