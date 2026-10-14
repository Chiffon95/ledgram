package com.gwolbisa.ledgram.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.util.scaledMarqueeSpeed
import kotlinx.coroutines.isActive

internal fun wrapMarqueeProgress(progress: Double): Double {
    if (!progress.isFinite()) return 0.0
    val wrapped = progress % 1.0
    return if (wrapped < 0.0) wrapped + 1.0 else wrapped
}

internal fun advanceMarqueeProgress(
    progress: Double,
    deltaSeconds: Double,
    speedPxPerSecond: Float,
    travelDistancePx: Float,
): Double {
    if (!deltaSeconds.isFinite() || deltaSeconds <= 0.0 ||
        !speedPxPerSecond.isFinite() || speedPxPerSecond <= 0f ||
        !travelDistancePx.isFinite() || travelDistancePx <= 0f
    ) {
        return wrapMarqueeProgress(progress)
    }
    return wrapMarqueeProgress(
        progress + deltaSeconds * speedPxPerSecond / travelDistancePx,
    )
}

internal fun marqueeTranslationForProgress(
    bounds: MarqueeTravelBounds,
    progress: Double,
): Float {
    val normalizedProgress = wrapMarqueeProgress(progress).toFloat()
    return bounds.startPx + (bounds.endPx - bounds.startPx) * normalizedProgress
}

@Composable
internal fun rememberMarqueeTranslation(
    motionEffect: MotionEffect,
    contentIdentity: Any,
    viewportWidthPx: Float,
    blockWidthPx: Float,
    baseSpeedPxPerSecond: Float,
    motionSpeedMultiplier: Float,
): Float {
    var progress by remember(motionEffect, contentIdentity) { mutableDoubleStateOf(0.0) }
    val currentTravelDistancePx by rememberUpdatedState(
        if (viewportWidthPx > 0f && blockWidthPx > 0f) {
            viewportWidthPx + blockWidthPx
        } else {
            0f
        },
    )
    val currentSpeedPxPerSecond by rememberUpdatedState(
        scaledMarqueeSpeed(baseSpeedPxPerSecond, motionSpeedMultiplier),
    )

    LaunchedEffect(motionEffect, contentIdentity) {
        var previousFrameNanos = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { currentFrameNanos ->
                val deltaSeconds = (currentFrameNanos - previousFrameNanos) / 1_000_000_000.0
                previousFrameNanos = currentFrameNanos
                progress = advanceMarqueeProgress(
                    progress = progress,
                    deltaSeconds = deltaSeconds,
                    speedPxPerSecond = currentSpeedPxPerSecond,
                    travelDistancePx = currentTravelDistancePx,
                )
            }
        }
    }

    val bounds = calculateMarqueeTravelBounds(
        viewportWidthPx = viewportWidthPx,
        unscaledTextWidthPx = blockWidthPx,
        motionEffect = motionEffect,
    )
    return marqueeTranslationForProgress(bounds, progress)
}
