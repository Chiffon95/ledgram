package com.gwolbisa.ledgram.util

const val MAX_MESSAGE_LENGTH = 100

fun normalizeMessageInput(input: String, maxCodePoints: Int = MAX_MESSAGE_LENGTH): String {
    val normalized = input
        .replace("\r\n", "\n")
        .replace('\r', '\n')
    val codePointCount = normalized.codePointCount(0, normalized.length)
    if (codePointCount <= maxCodePoints) return normalized

    val endIndex = normalized.offsetByCodePoints(0, maxCodePoints)
    return normalized.substring(0, endIndex)
}

fun normalizeMessageForDisplay(input: String): String {
    val normalized = normalizeMessageInput(input)
    return if (normalized.isBlank()) {
        ""
    } else {
        normalized.trim { character -> character != '\n' && character.isWhitespace() }
    }
}

fun String.codePointLength(): Int = codePointCount(0, length)

