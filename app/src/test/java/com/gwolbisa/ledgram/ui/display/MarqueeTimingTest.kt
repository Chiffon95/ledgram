package com.gwolbisa.ledgram.ui.display

import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.ui.components.TextGlowEffectDefaults
import com.gwolbisa.ledgram.ui.components.calculateMarqueeDurationMillis
import com.gwolbisa.ledgram.ui.components.calculateMarqueeTravelBounds
import com.gwolbisa.ledgram.ui.components.visualEffectTransformOrigin
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

    @Test
    fun `travel bounds use viewport unscaled text width and direction only`() {
        val rightToLeft = calculateMarqueeTravelBounds(
            viewportWidthPx = 800f,
            unscaledTextWidthPx = 300f,
            motionEffect = MotionEffect.RIGHT_TO_LEFT,
        )
        val leftToRight = calculateMarqueeTravelBounds(
            viewportWidthPx = 800f,
            unscaledTextWidthPx = 300f,
            motionEffect = MotionEffect.LEFT_TO_RIGHT,
        )

        assertEquals(800f, rightToLeft.startPx)
        assertEquals(-300f, rightToLeft.endPx)
        assertEquals(-300f, leftToRight.startPx)
        assertEquals(800f, leftToRight.endPx)
    }

    @Test
    fun `multiline block uses the same symmetric bounds for the same layout width`() {
        val singleLine = calculateMarqueeTravelBounds(
            viewportWidthPx = 1_000f,
            unscaledTextWidthPx = 600f,
            motionEffect = MotionEffect.RIGHT_TO_LEFT,
        )
        val multiline = calculateMarqueeTravelBounds(
            viewportWidthPx = 1_000f,
            unscaledTextWidthPx = 600f,
            motionEffect = MotionEffect.RIGHT_TO_LEFT,
        )
        val reverse = calculateMarqueeTravelBounds(
            viewportWidthPx = 1_000f,
            unscaledTextWidthPx = 600f,
            motionEffect = MotionEffect.LEFT_TO_RIGHT,
        )

        assertEquals(singleLine, multiline)
        assertEquals(1_000f, multiline.startPx)
        assertEquals(-600f, multiline.endPx)
        assertEquals(-600f, reverse.startPx)
        assertEquals(1_000f, reverse.endPx)
        assertEquals(
            multiline.startPx - multiline.endPx,
            reverse.endPx - reverse.startPx,
        )
    }

    @Test
    fun `visual effect pivot follows motion direction`() {
        val staticOrigin = visualEffectTransformOrigin(MotionEffect.STATIC)
        val rightToLeftOrigin = visualEffectTransformOrigin(MotionEffect.RIGHT_TO_LEFT)
        val leftToRightOrigin = visualEffectTransformOrigin(MotionEffect.LEFT_TO_RIGHT)

        assertEquals(0.5f, staticOrigin.pivotFractionX)
        assertEquals(0.5f, staticOrigin.pivotFractionY)
        assertEquals(0f, rightToLeftOrigin.pivotFractionX)
        assertEquals(0.5f, rightToLeftOrigin.pivotFractionY)
        assertEquals(1f, leftToRightOrigin.pivotFractionX)
        assertEquals(0.5f, leftToRightOrigin.pivotFractionY)
    }
}
