package com.gwolbisa.ledgram.model

import android.os.Parcelable
import com.gwolbisa.ledgram.util.DEFAULT_SPEED_MULTIPLIER
import kotlinx.parcelize.Parcelize

@Parcelize
data class DisplayConfig(
    val message: String,
    val textColor: Long,
    val backgroundColor: Long,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean,
    val renderStyle: RenderStyle,
    val motionEffect: MotionEffect,
    val visualEffect: VisualEffect,
    val motionSpeedMultiplier: Float = DEFAULT_SPEED_MULTIPLIER,
    val effectSpeedMultiplier: Float = DEFAULT_SPEED_MULTIPLIER,
) : Parcelable

enum class RenderStyle {
    TEXT_GLOW,
    DOT_MATRIX,
}

enum class MotionEffect {
    STATIC,
    RIGHT_TO_LEFT,
    LEFT_TO_RIGHT,
}

enum class VisualEffect {
    NONE,
    BLINK,
    PULSE,
    FADE,
}

