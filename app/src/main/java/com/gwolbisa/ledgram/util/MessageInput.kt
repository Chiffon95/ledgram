package com.gwolbisa.ledgram.util

const val MAX_MESSAGE_LENGTH = 100

fun normalizeMessageInput(input: String, maxCodePoints: Int = MAX_MESSAGE_LENGTH): String {
    val singleLine = input.replace(Regex("[\\r\\n]+"), " ")
    val codePointCount = singleLine.codePointCount(0, singleLine.length)
    if (codePointCount <= maxCodePoints) return singleLine

    val endIndex = singleLine.offsetByCodePoints(0, maxCodePoints)
    return singleLine.substring(0, endIndex)
}

fun String.codePointLength(): Int = codePointCount(0, length)

