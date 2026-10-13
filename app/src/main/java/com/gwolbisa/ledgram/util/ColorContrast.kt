package com.gwolbisa.ledgram.util

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private const val MINIMUM_READABLE_CONTRAST_RATIO = 4.5

fun isLowColorContrast(first: Long, second: Long): Boolean =
    colorContrastRatio(first, second) < MINIMUM_READABLE_CONTRAST_RATIO

fun colorContrastRatio(first: Long, second: Long): Double {
    val firstLuminance = relativeLuminance(first)
    val secondLuminance = relativeLuminance(second)
    return (max(firstLuminance, secondLuminance) + 0.05) /
        (min(firstLuminance, secondLuminance) + 0.05)
}

private fun relativeLuminance(color: Long): Double {
    val red = linearizedChannel((color shr 16) and 0xFF)
    val green = linearizedChannel((color shr 8) and 0xFF)
    val blue = linearizedChannel(color and 0xFF)
    return 0.2126 * red + 0.7152 * green + 0.0722 * blue
}

private fun linearizedChannel(channel: Long): Double {
    val normalized = channel / 255.0
    return if (normalized <= 0.04045) {
        normalized / 12.92
    } else {
        ((normalized + 0.055) / 1.055).pow(2.4)
    }
}
