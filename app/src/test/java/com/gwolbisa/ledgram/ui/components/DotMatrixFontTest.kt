package com.gwolbisa.ledgram.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixFontTest {
    @Test
    fun `known glyph has fixed five by seven dimensions`() {
        val glyph = dotMatrixGlyphFor('A')

        assertEquals(DOT_MATRIX_GLYPH_HEIGHT, glyph.rows.size)
        assertTrue(glyph.isActive(0, 1))
        assertFalse(glyph.isActive(0, 0))
    }

    @Test
    fun `lowercase maps to uppercase glyph`() {
        assertSame(dotMatrixGlyphFor('A'), dotMatrixGlyphFor('a'))
        assertSame(dotMatrixGlyphFor('Z'), dotMatrixGlyphFor('z'))
    }

    @Test
    fun `unsupported characters use question mark fallback`() {
        assertSame(dotMatrixGlyphFor('?'), dotMatrixGlyphFor('한'))
        assertSame(dotMatrixGlyphFor('?'), dotMatrixGlyphFor(0x1F600))
    }

    @Test
    fun `space glyph contains no active dots`() {
        val space = dotMatrixGlyphFor(' ')

        for (row in 0 until DOT_MATRIX_GLYPH_HEIGHT) {
            for (column in 0 until DOT_MATRIX_GLYPH_WIDTH) {
                assertFalse(space.isActive(row, column))
            }
        }
    }

    @Test
    fun `line splitting preserves explicit empty lines`() {
        assertEquals(listOf("A", "", "B", ""), splitDotMatrixLines("A\n\nB\n"))
    }
}
