package com.gwolbisa.ledgram.util

import kotlin.math.roundToInt

internal const val MIN_SPEED_MULTIPLIER = 0.25f
internal const val MAX_SPEED_MULTIPLIER = 5f
internal const val DEFAULT_SPEED_MULTIPLIER = 1f

internal val SUPPORTED_SPEED_MULTIPLIERS = listOf(
    0.25f,
    0.5f,
    0.75f,
    1f,
    1.25f,
    1.5f,
    2f,
    2.5f,
    3f,
    4f,
    5f,
)

internal fun normalizeSpeedMultiplier(multiplier: Float): Float =
    if (multiplier.isFinite()) {
        multiplier.coerceIn(MIN_SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER)
    } else {
        DEFAULT_SPEED_MULTIPLIER
    }

internal fun normalizeMotionSpeedMultiplier(multiplier: Float): Float =
    normalizeSpeedMultiplier(multiplier)

internal fun normalizeEffectSpeedMultiplier(multiplier: Float): Float =
    normalizeSpeedMultiplier(multiplier)

internal fun scaledEffectDurationMillis(
    baseDurationMillis: Int,
    effectSpeedMultiplier: Float,
): Int = (baseDurationMillis / normalizeEffectSpeedMultiplier(effectSpeedMultiplier))
    .roundToInt()
    .coerceAtLeast(1)

internal fun scaledMarqueeSpeed(
    baseSpeed: Float,
    motionSpeedMultiplier: Float,
): Float {
    if (!baseSpeed.isFinite() || baseSpeed <= 0f) return 0f
    return baseSpeed * normalizeMotionSpeedMultiplier(motionSpeedMultiplier)
}
