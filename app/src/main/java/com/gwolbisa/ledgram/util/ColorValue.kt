package com.gwolbisa.ledgram.util

import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

data class HsvColor(
    val hue: Float,
    val saturation: Float,
    val value: Float,
)

private val HEX_COLOR_PATTERN = Regex("^[0-9A-Fa-f]{6}$")

fun argbToHsv(color: Long): HsvColor {
    val red = ((color shr 16) and 0xFF).toFloat() / 255f
    val green = ((color shr 8) and 0xFF).toFloat() / 255f
    val blue = (color and 0xFF).toFloat() / 255f
    val maximum = max(red, max(green, blue))
    val minimum = min(red, min(green, blue))
    val delta = maximum - minimum

    val rawHue = when {
        delta == 0f -> 0f
        maximum == red -> 60f * (((green - blue) / delta) % 6f)
        maximum == green -> 60f * (((blue - red) / delta) + 2f)
        else -> 60f * (((red - green) / delta) + 4f)
    }

    return HsvColor(
        hue = if (rawHue < 0f) rawHue + 360f else rawHue,
        saturation = if (maximum == 0f) 0f else delta / maximum,
        value = maximum,
    )
}

fun hsvToArgb(hsv: HsvColor): Long {
    val hue = ((hsv.hue % 360f) + 360f) % 360f
    val saturation = hsv.saturation.coerceIn(0f, 1f)
    val value = hsv.value.coerceIn(0f, 1f)
    val chroma = value * saturation
    val secondary = chroma * (1f - abs((hue / 60f) % 2f - 1f))
    val match = value - chroma
    val (red, green, blue) = when {
        hue < 60f -> Triple(chroma, secondary, 0f)
        hue < 120f -> Triple(secondary, chroma, 0f)
        hue < 180f -> Triple(0f, chroma, secondary)
        hue < 240f -> Triple(0f, secondary, chroma)
        hue < 300f -> Triple(secondary, 0f, chroma)
        else -> Triple(chroma, 0f, secondary)
    }

    val redChannel = ((red + match) * 255f).roundToInt().coerceIn(0, 255)
    val greenChannel = ((green + match) * 255f).roundToInt().coerceIn(0, 255)
    val blueChannel = ((blue + match) * 255f).roundToInt().coerceIn(0, 255)
    return 0xFF000000L or
        (redChannel.toLong() shl 16) or
        (greenChannel.toLong() shl 8) or
        blueChannel.toLong()
}

fun parseHexColor(input: String): Long? {
    val digits = input.trim().removePrefix("#")
    if (!HEX_COLOR_PATTERN.matches(digits)) return null
    return 0xFF000000L or digits.toLong(16)
}

fun formatHexColor(color: Long): String =
    String.format(Locale.ROOT, "#%06X", color and 0xFFFFFFL)
