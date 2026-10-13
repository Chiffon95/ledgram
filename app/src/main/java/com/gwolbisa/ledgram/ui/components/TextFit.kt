package com.gwolbisa.ledgram.ui.components

private const val MINIMUM_FIT_FONT_SIZE_SP = 0.01f
private const val FONT_FIT_SEARCH_STEPS = 16

internal fun findLargestFittingFontSize(
    maximumFontSizeSp: Float,
    fits: (Float) -> Boolean,
): Float {
    val maximum = if (maximumFontSizeSp.isFinite()) {
        maximumFontSizeSp.coerceAtLeast(MINIMUM_FIT_FONT_SIZE_SP)
    } else {
        MINIMUM_FIT_FONT_SIZE_SP
    }
    if (fits(maximum)) return maximum
    if (!fits(MINIMUM_FIT_FONT_SIZE_SP)) return MINIMUM_FIT_FONT_SIZE_SP

    var lower = MINIMUM_FIT_FONT_SIZE_SP
    var upper = maximum
    repeat(FONT_FIT_SEARCH_STEPS) {
        val candidate = (lower + upper) / 2f
        if (fits(candidate)) {
            lower = candidate
        } else {
            upper = candidate
        }
    }
    return lower
}

internal fun isCompleteStaticTextLayout(
    textLength: Int,
    lineCount: Int,
    lastLineEnd: Int,
    didOverflowWidth: Boolean,
    didOverflowHeight: Boolean,
): Boolean = lineCount >= 1 &&
    lastLineEnd >= textLength &&
    !didOverflowWidth &&
    !didOverflowHeight
