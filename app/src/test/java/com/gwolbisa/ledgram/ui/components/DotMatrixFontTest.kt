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

        assertEquals(DOT_MATRIX_GLYPH_WIDTH, glyph.width)
        assertEquals(DOT_MATRIX_GLYPH_HEIGHT, glyph.height)
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
    fun `unsupported supplementary character uses question mark fallback`() {
        assertSame(dotMatrixGlyphFor('?'), dotMatrixGlyphFor(0x1F600))
    }

    @Test
    fun `ga is an eleven by eleven Hangul glyph instead of fallback`() {
        val decomposition = decomposeHangulSyllable('가'.code)
        val glyph = dotMatrixGlyphFor('가')

        assertEquals(HangulVowelLayout.VERTICAL, decomposition?.vowelLayout)
        assertFalse(decomposition?.hasFinal ?: true)
        assertEquals(DOT_MATRIX_HANGUL_GLYPH_WIDTH, glyph.width)
        assertEquals(DOT_MATRIX_HANGUL_GLYPH_HEIGHT, glyph.height)
        assertFalse(glyph == FALLBACK_GLYPH)
    }

    @Test
    fun `han has a final consonant and never falls back`() {
        val decomposition = decomposeHangulSyllable('한'.code)

        assertTrue(decomposition?.hasFinal == true)
        assertFalse(dotMatrixGlyphFor('한') == FALLBACK_GLYPH)
    }

    @Test
    fun `geul uses horizontal vowel layout with a final consonant`() {
        val decomposition = decomposeHangulSyllable('글'.code)

        assertEquals(HangulVowelLayout.HORIZONTAL, decomposition?.vowelLayout)
        assertTrue(decomposition?.hasFinal == true)
        assertFalse(dotMatrixGlyphFor('글') == FALLBACK_GLYPH)
    }

    @Test
    fun `compound vowel syllable has dedicated composition layout`() {
        val decomposition = decomposeHangulSyllable('광'.code)

        assertEquals(HangulVowelLayout.COMPOUND, decomposition?.vowelLayout)
        assertTrue(decomposition?.hasFinal == true)
        assertFalse(dotMatrixGlyphFor('광') == FALLBACK_GLYPH)
    }

    @Test
    fun `annyeonghaseyo produces five non fallback Hangul glyphs`() {
        val glyphs = "안녕하세요".codePoints().toArray().map(::dotMatrixGlyphFor)

        assertEquals(5, glyphs.size)
        assertTrue(glyphs.all { it.width == 11 && it.height == 11 && it != FALLBACK_GLYPH })
    }

    @Test
    fun `every modern precomposed Hangul syllable composes without fallback`() {
        (0xAC00..0xD7A3).forEach { codePoint ->
            val glyph = dotMatrixGlyphFor(codePoint)
            assertEquals(DOT_MATRIX_HANGUL_GLYPH_WIDTH, glyph.width)
            assertEquals(DOT_MATRIX_HANGUL_GLYPH_HEIGHT, glyph.height)
            assertFalse(glyph == FALLBACK_GLYPH)
        }
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
