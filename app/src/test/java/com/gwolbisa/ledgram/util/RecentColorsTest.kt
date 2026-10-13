package com.gwolbisa.ledgram.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentColorsTest {
    @Test
    fun `newest color is first and duplicate is removed`() {
        val initial = listOf(0xFFFF0000, 0xFF00FF00, 0xFF0000FF)

        val result = addRecentColor(initial, 0xFF00FF00)

        assertEquals(listOf(0xFF00FF00, 0xFFFF0000, 0xFF0000FF), result)
    }

    @Test
    fun `recent colors are limited to eight`() {
        val initial = (0L until MAX_RECENT_COLORS.toLong()).map { 0xFF000000L or it }

        val result = addRecentColor(initial, 0xFFFFFFFF)

        assertEquals(MAX_RECENT_COLORS, result.size)
        assertEquals(0xFFFFFFFF, result.first())
        assertEquals(initial.take(MAX_RECENT_COLORS - 1), result.drop(1))
    }
}
