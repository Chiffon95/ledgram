package com.gwolbisa.ledgram.util

import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorValueTest {
    @Test
    fun `hex parser accepts supported forms`() {
        assertEquals(0xFFFFFFFF, parseHexColor("#FFFFFF"))
        assertEquals(0xFFFFFFFF, parseHexColor("ffffff"))
        assertEquals(0xFF000000, parseHexColor("000000"))
    }

    @Test
    fun `hex parser rejects invalid length and characters`() {
        assertNull(parseHexColor("#FFFFF"))
        assertNull(parseHexColor("#FFFFFFF"))
        assertNull(parseHexColor("GG0000"))
    }

    @Test
    fun `hex formatter emits uppercase rgb without alpha`() {
        assertEquals("#FFFFFF", formatHexColor(0xFFFFFFFF))
        assertEquals("#000000", formatHexColor(0xFF000000))
        assertEquals("#AF52DE", formatHexColor(0x12AF52DE))
    }

    @Test
    fun `primary colors round trip through hsv`() {
        assertEquals(0xFFFF0000, hsvToArgb(argbToHsv(0xFFFF0000)))
        assertEquals(0xFF00FF00, hsvToArgb(argbToHsv(0xFF00FF00)))
        assertEquals(0xFF0000FF, hsvToArgb(argbToHsv(0xFF0000FF)))
    }

    @Test
    fun `arbitrary color round trip stays within one channel step`() {
        val original = 0xFF34A2C7L
        val result = hsvToArgb(argbToHsv(original))

        assertChannelsWithinTolerance(original, result, tolerance = 1)
    }

    private fun assertChannelsWithinTolerance(expected: Long, actual: Long, tolerance: Int) {
        listOf(16, 8, 0).forEach { shift ->
            val expectedChannel = (expected shr shift) and 0xFF
            val actualChannel = (actual shr shift) and 0xFF
            assertTrue(abs(expectedChannel - actualChannel) <= tolerance.toLong())
        }
    }
}
