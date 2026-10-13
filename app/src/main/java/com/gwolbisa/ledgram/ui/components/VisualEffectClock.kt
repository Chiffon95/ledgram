package com.gwolbisa.ledgram.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.util.normalizeEffectSpeedMultiplier
import kotlinx.coroutines.isActive

internal data class VisualTransform(
    val alpha: Float = 1f,
    val scale: Float = 1f,
)

internal fun wrapVisualEffectPhase(phase: Double): Double {
    if (!phase.isFinite()) return 0.0
    val wrapped = phase % 1.0
    return if (wrapped < 0.0) wrapped + 1.0 else wrapped
}

internal fun advanceVisualEffectPhase(
    phase: Double,
    deltaSeconds: Double,
    speedMultiplier: Float,
    cycleDurationSeconds: Double,
): Double {
    if (!deltaSeconds.isFinite() || deltaSeconds <= 0.0 ||
        !cycleDurationSeconds.isFinite() || cycleDurationSeconds <= 0.0
    ) {
        return wrapVisualEffectPhase(phase)
    }
    val normalizedSpeed = normalizeEffectSpeedMultiplier(speedMultiplier).toDouble()
    return wrapVisualEffectPhase(
        phase + deltaSeconds * normalizedSpeed / cycleDurationSeconds,
    )
}

internal fun blinkAlphaForPhase(phase: Double): Float =
    if (wrapVisualEffectPhase(phase) < 0.65) 1f else 0f

internal fun reversibleProgressFromPhase(phase: Double): Float {
    val wrapped = wrapVisualEffectPhase(phase).toFloat()
    return if (wrapped <= 0.5f) wrapped * 2f else (1f - wrapped) * 2f
}

internal fun pulseTransformForPhase(phase: Double): VisualTransform {
    val progress = FastOutSlowInEasing.transform(reversibleProgressFromPhase(phase))
    return VisualTransform(
        alpha = lerp(1f, TextGlowEffectDefaults.PULSE_MIN_ALPHA, progress),
        scale = lerp(1f, TextGlowEffectDefaults.PULSE_MIN_SCALE, progress),
    )
}

internal fun fadeAlphaForPhase(phase: Double): Float = reversibleProgressFromPhase(phase)

internal fun visualTransformForPhase(
    effect: VisualEffect,
    phase: Double,
): VisualTransform = when (effect) {
    VisualEffect.NONE -> VisualTransform()
    VisualEffect.BLINK -> VisualTransform(alpha = blinkAlphaForPhase(phase))
    VisualEffect.PULSE -> pulseTransformForPhase(phase)
    VisualEffect.FADE -> VisualTransform(alpha = fadeAlphaForPhase(phase))
}

private fun visualEffectCycleDurationSeconds(effect: VisualEffect): Double = when (effect) {
    VisualEffect.NONE -> 1.0
    VisualEffect.BLINK ->
        (TextGlowEffectDefaults.BLINK_VISIBLE_MILLIS +
            TextGlowEffectDefaults.BLINK_HIDDEN_MILLIS) / 1_000.0

    VisualEffect.PULSE -> TextGlowEffectDefaults.PULSE_HALF_CYCLE_MILLIS * 2 / 1_000.0
    VisualEffect.FADE -> TextGlowEffectDefaults.FADE_HALF_CYCLE_MILLIS * 2 / 1_000.0
}

@Composable
internal fun rememberVisualTransform(
    effect: VisualEffect,
    effectSpeedMultiplier: Float,
): VisualTransform {
    if (effect == VisualEffect.NONE) return VisualTransform()

    var phase by remember(effect) { mutableDoubleStateOf(0.0) }
    val currentSpeedMultiplier by rememberUpdatedState(
        normalizeEffectSpeedMultiplier(effectSpeedMultiplier),
    )

    LaunchedEffect(effect) {
        var previousFrameNanos = withFrameNanos { it }
        while (isActive) {
            withFrameNanos { currentFrameNanos ->
                val deltaSeconds = (currentFrameNanos - previousFrameNanos) / 1_000_000_000.0
                previousFrameNanos = currentFrameNanos
                phase = advanceVisualEffectPhase(
                    phase = phase,
                    deltaSeconds = deltaSeconds,
                    speedMultiplier = currentSpeedMultiplier,
                    cycleDurationSeconds = visualEffectCycleDurationSeconds(effect),
                )
            }
        }
    }

    return visualTransformForPhase(effect, phase)
}

private fun lerp(start: Float, end: Float, fraction: Float): Float =
    start + (end - start) * fraction
