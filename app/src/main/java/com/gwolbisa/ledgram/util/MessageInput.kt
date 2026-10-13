package com.gwolbisa.ledgram.util

import com.gwolbisa.ledgram.model.MotionEffect

const val MAX_MESSAGE_LENGTH = 100

fun normalizeMessageInput(input: String, maxCodePoints: Int = MAX_MESSAGE_LENGTH): String {
    val canonicalLineEndings = input
        .replace("\r\n", "\n")
        .replace('\r', '\n')
    val normalized = buildString(canonicalLineEndings.length) {
        var preservedNewline = false
        canonicalLineEndings.forEach { character ->
            if (character != '\n') {
                append(character)
            } else if (!preservedNewline) {
                append('\n')
                preservedNewline = true
            } else if (isNotEmpty() && !last().isWhitespace()) {
                append(' ')
            }
        }
    }
    val codePointCount = normalized.codePointCount(0, normalized.length)
    if (codePointCount <= maxCodePoints) return normalized

    val endIndex = normalized.offsetByCodePoints(0, maxCodePoints)
    return normalized.substring(0, endIndex)
}

fun normalizeMessageForStaticDisplay(input: String): String =
    normalizeMessageInput(input).trim()

fun normalizeMessageForMarqueeDisplay(input: String): String =
    normalizeMessageForStaticDisplay(input).replace('\n', ' ')

fun normalizeMessageForDisplay(input: String, motionEffect: MotionEffect): String =
    when (motionEffect) {
        MotionEffect.STATIC -> normalizeMessageForStaticDisplay(input)
        MotionEffect.RIGHT_TO_LEFT,
        MotionEffect.LEFT_TO_RIGHT,
        -> normalizeMessageForMarqueeDisplay(input)
    }

fun String.codePointLength(): Int = codePointCount(0, length)

