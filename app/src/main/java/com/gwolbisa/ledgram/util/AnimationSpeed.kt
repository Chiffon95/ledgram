package com.gwolbisa.ledgram.util

import kotlin.math.roundToInt

internal const val MIN_SPEED_MULTIPLIER = 0.5f
internal const val MAX_SPEED_MULTIPLIER = 2f
internal const val DEFAULT_SPEED_MULTIPLIER = 1f

internal fun normalizeSpeedMultiplier(multiplier: Float): Float =
    if (multiplier.isFinite()) {
        multiplier.coerceIn(MIN_SPEED_MULTIPLIER, MAX_SPEED_MULTIPLIER)
    } else {
        DEFAULT_SPEED_MULTIPLIER
    }

internal fun scaledAnimationDurationMillis(
    baseDurationMillis: Int,
    multiplier: Float,
): Int = (baseDurationMillis / normalizeSpeedMultiplier(multiplier))
    .roundToInt()
    .coerceAtLeast(1)

internal fun scaledAnimationSpeed(
    baseSpeed: Float,
    multiplier: Float,
): Float = baseSpeed * normalizeSpeedMultiplier(multiplier)
