package com.gwolbisa.ledgram.util

import com.gwolbisa.ledgram.model.MotionEffect

const val MAX_MESSAGE_LENGTH = 100
private val DISPLAY_WHITESPACE = Regex("\\s+")

fun normalizeMessageInput(input: String, maxCodePoints: Int = MAX_MESSAGE_LENGTH): String {
    val normalized = input
        .replace("\r\n", "\n")
        .replace('\r', '\n')
    val codePointCount = normalized.codePointCount(0, normalized.length)
    if (codePointCount <= maxCodePoints) return normalized

    val endIndex = normalized.offsetByCodePoints(0, maxCodePoints)
    return normalized.substring(0, endIndex)
}

fun normalizeMessageForStaticDisplay(input: String): String {
    val normalized = normalizeMessageInput(input)
    return if (normalized.isBlank()) {
        ""
    } else {
        normalized.trim { character -> character != '\n' && character.isWhitespace() }
    }
}

fun normalizeMessageForMarqueeDisplay(input: String): String =
    normalizeMessageForStaticDisplay(input)
        .replace(DISPLAY_WHITESPACE, " ")
        .trim()

fun normalizeMessageForDisplay(input: String, motionEffect: MotionEffect): String =
    when (motionEffect) {
        MotionEffect.STATIC -> normalizeMessageForStaticDisplay(input)
        MotionEffect.RIGHT_TO_LEFT,
        MotionEffect.LEFT_TO_RIGHT,
        -> normalizeMessageForMarqueeDisplay(input)
    }

fun String.codePointLength(): Int = codePointCount(0, length)

