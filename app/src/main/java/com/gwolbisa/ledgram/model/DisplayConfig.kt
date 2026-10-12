package com.gwolbisa.ledgram.model

import android.os.Parcelable
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
    val effect: DisplayEffect,
) : Parcelable

enum class RenderStyle {
    TEXT_GLOW,
    DOT_MATRIX,
}

enum class DisplayEffect {
    STATIC,
    RIGHT_TO_LEFT,
    LEFT_TO_RIGHT,
    BLINK,
    PULSE,
    FADE,
}

