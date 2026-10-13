package com.gwolbisa.ledgram.util

const val MAX_RECENT_COLORS = 8

fun addRecentColor(
    recentColors: List<Long>,
    color: Long,
    maximumSize: Int = MAX_RECENT_COLORS,
): List<Long> {
    val opaqueColor = 0xFF000000L or (color and 0xFFFFFFL)
    return (listOf(opaqueColor) + recentColors.filterNot { it == opaqueColor })
        .take(maximumSize)
}
