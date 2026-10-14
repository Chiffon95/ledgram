package com.gwolbisa.ledgram.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixGlyphTest {
    @Test
    fun `glyph carries variable geometry and baseline`() {
        val glyph = dotMatrixGlyph(".#.", "###", baseline = 1)

        assertEquals(3, glyph.width)
        assertEquals(2, glyph.height)
        assertEquals(1, glyph.baseline)
        assertTrue(glyph.isActive(0, 1))
        assertFalse(glyph.isActive(0, 0))
    }

    @Test
    fun `fallback glyph is a single question mark matrix`() {
        assertEquals(5, FALLBACK_GLYPH.width)
        assertEquals(7, FALLBACK_GLYPH.height)
        assertTrue(FALLBACK_GLYPH.rows.any { it != 0 })
    }

    @Test
    fun `line splitting preserves explicit empty lines`() {
        assertEquals(listOf("A", "", "B", ""), splitDotMatrixLines("A\n\nB\n"))
    }
}
