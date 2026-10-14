package com.gwolbisa.ledgram.ui.display

import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.ui.components.advanceMarqueeProgress
import com.gwolbisa.ledgram.ui.components.calculateMarqueeTravelBounds
import com.gwolbisa.ledgram.ui.components.marqueeTranslationForProgress
import com.gwolbisa.ledgram.ui.components.visualEffectTransformOrigin
import com.gwolbisa.ledgram.ui.components.wrapMarqueeProgress
import com.gwolbisa.ledgram.util.scaledMarqueeSpeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarqueeTimingTest {
    @Test
    fun `progress wraps naturally after a complete travel`() {
        assertEquals(0.25, wrapMarqueeProgress(1.25), 0.000001)
        assertEquals(0.75, wrapMarqueeProgress(-0.25), 0.000001)
        assertEquals(
            0.05,
            advanceMarqueeProgress(0.95, 1.0, 100f, 1_000f),
            0.000001,
        )
    }

    @Test
    fun `invalid geometry or speed preserves current progress`() {
        assertEquals(0.4, advanceMarqueeProgress(0.4, 1.0, 0f, 1_000f), 0.0)
        assertEquals(0.4, advanceMarqueeProgress(0.4, 1.0, 100f, 0f), 0.0)
        assertEquals(0.4, advanceMarqueeProgress(0.4, 0.0, 100f, 1_000f), 0.0)
    }

    @Test
    fun `speed changes increment rate without replacing current progress`() {
        val currentProgress = 0.4
        val slow = advanceMarqueeProgress(currentProgress, 1.0, 25f, 1_000f)
        val normal = advanceMarqueeProgress(currentProgress, 1.0, 100f, 1_000f)
        val fast = advanceMarqueeProgress(currentProgress, 1.0, 500f, 1_000f)

        assertEquals(0.425, slow, 0.000001)
        assertEquals(0.5, normal, 0.000001)
        assertEquals(0.9, fast, 0.000001)
        assertEquals(
            currentProgress,
            advanceMarqueeProgress(currentProgress, 0.0, 500f, 1_000f),
            0.0,
        )
    }

    @Test
    fun `repeated multiplier changes continue from accumulated progress`() {
        var progress = 0.4
        listOf(0.25f, 5f, 0.5f, 3f).forEach { multiplier ->
            progress = advanceMarqueeProgress(
                progress = progress,
                deltaSeconds = 0.1,
                speedPxPerSecond = scaledMarqueeSpeed(90f, multiplier),
                travelDistancePx = 1_000f,
            )
        }

        assertEquals(0.47875, progress, 0.000001)
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
    fun `geometry changes remap the same progress instead of returning to start`() {
        val progress = 0.4
        val originalBounds = calculateMarqueeTravelBounds(1_000f, 600f, MotionEffect.RIGHT_TO_LEFT)
        val widerBounds = calculateMarqueeTravelBounds(1_000f, 800f, MotionEffect.RIGHT_TO_LEFT)

        val originalPosition = marqueeTranslationForProgress(originalBounds, progress)
        val widerPosition = marqueeTranslationForProgress(widerBounds, progress)

        assertEquals(360f, originalPosition, 0.001f)
        assertEquals(280f, widerPosition, 0.001f)
        assertTrue(widerPosition != widerBounds.startPx)
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
