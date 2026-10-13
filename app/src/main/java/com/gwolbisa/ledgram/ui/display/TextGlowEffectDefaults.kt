package com.gwolbisa.ledgram.ui.display

import kotlin.math.roundToInt

internal object TextGlowEffectDefaults {
    const val BLINK_VISIBLE_MILLIS = 650
    const val BLINK_HIDDEN_MILLIS = 350
    const val PULSE_HALF_CYCLE_MILLIS = 700
    const val FADE_HALF_CYCLE_MILLIS = 900
    const val PULSE_MIN_ALPHA = 0.55f
    const val PULSE_MIN_SCALE = 0.94f
    const val MARQUEE_SPEED_DP_PER_SECOND = 90f
    const val MIN_MARQUEE_DURATION_MILLIS = 1_000
}

internal fun calculateMarqueeDurationMillis(
    travelDistancePx: Float,
    speedPxPerSecond: Float,
): Int {
    if (!travelDistancePx.isFinite() || travelDistancePx <= 0f ||
        !speedPxPerSecond.isFinite() || speedPxPerSecond <= 0f
    ) {
        return TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS
    }

    val duration = travelDistancePx / speedPxPerSecond * 1_000f
    return duration
        .coerceAtMost(Int.MAX_VALUE.toFloat())
        .roundToInt()
        .coerceAtLeast(TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS)
}
