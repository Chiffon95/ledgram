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
    fun `alpha crop removes transparent top rows`() {
        val glyph = crop(
            "...",
            ".#.",
            ".#.",
            baseline = 3,
        )

        assertEquals(2, glyph.height)
        assertEquals(2, glyph.baseline)
    }

    @Test
    fun `alpha crop removes transparent bottom rows`() {
        val glyph = crop(
            ".#.",
            ".#.",
            "...",
            baseline = 2,
        )

        assertEquals(2, glyph.height)
        assertEquals(2, glyph.baseline)
    }

    @Test
    fun `alpha crop removes transparent left and right columns`() {
        val glyph = crop(".###.", baseline = 1)

        assertEquals(3, glyph.width)
        assertEquals(listOf(0b111), glyph.rows)
    }

    @Test
    fun `alpha crop adjusts baseline by cropped top`() {
        val glyph = crop(
            ".....",
            ".###.",
            ".#.#.",
            ".....",
            baseline = 3,
        )

        assertEquals(2, glyph.height)
        assertEquals(2, glyph.baseline)
    }

    @Test
    fun `blank alpha mask preserves space advance and line box`() {
        val glyph = crop(
            ".....",
            ".....",
            ".....",
            baseline = 2,
        )

        assertEquals(5, glyph.width)
        assertEquals(3, glyph.height)
        assertEquals(2, glyph.baseline)
        assertTrue(glyph.rows.all { it == 0 })
    }

    @Test
    fun `cropped fallback remains a valid active glyph`() {
        val glyph = crop(
            ".......",
            "..###..",
            ".#...#.",
            "....#..",
            "...#...",
            ".......",
            "...#...",
            ".......",
            baseline = 7,
        )

        assertEquals(5, glyph.width)
        assertEquals(6, glyph.height)
        assertTrue(glyph.rows.any { it != 0 })
    }

    @Test
    fun `line splitting preserves explicit empty lines`() {
        assertEquals(listOf("A", "", "B", ""), splitDotMatrixLines("A\n\nB\n"))
    }

    private fun crop(vararg rows: String, baseline: Int): DotMatrixGlyph {
        val width = rows.first().length
        val pixels = rows.flatMap { row ->
            require(row.length == width)
            row.map { pixel -> if (pixel == '#') 0xFF000000.toInt() else 0 }
        }.toIntArray()
        return croppedAlphaMaskGlyph(
            pixels = pixels,
            width = width,
            height = rows.size,
            baseline = baseline,
            activeAlphaThreshold = 128,
        )
    }
}
