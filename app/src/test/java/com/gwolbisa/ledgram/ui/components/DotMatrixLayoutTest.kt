package com.gwolbisa.ledgram.ui.components

import com.gwolbisa.ledgram.model.MotionEffect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixLayoutTest {
    private val latinUpper = solidGlyph(width = 5, height = 8, baseline = 6)
    private val latinLower = solidGlyph(width = 4, height = 8, baseline = 6)
    private val hangul = solidGlyph(width = 12, height = 16, baseline = 13)
    private val jamo = solidGlyph(width = 8, height = 16, baseline = 13)

    private val fakeProvider: (Int) -> DotMatrixGlyph = { codePoint ->
        when (codePoint) {
            in 'A'.code..'Z'.code -> latinUpper
            in 'a'.code..'z'.code -> latinLower
            in '0'.code..'9'.code, ' '.code, '!'.code, '?'.code -> latinUpper
            in 0xAC00..0xD7A3 -> hangul
            in 0x3131..0x318E, in 0x1100..0x11FF -> jamo
            else -> FALLBACK_GLYPH
        }
    }

    @Test
    fun `lowercase reaches provider without uppercase normalization`() {
        val requestedCodePoints = mutableListOf<Int>()
        val layout = createDotMatrixBlockLayout("AbC", glyphProvider = { codePoint ->
            requestedCodePoints += codePoint
            fakeProvider(codePoint)
        })

        assertEquals(listOf('A'.code, 'b'.code, 'C'.code), requestedCodePoints)
        assertEquals(latinLower.width, layout.lines.single().glyphs[1].width)
        assertNotEquals(layout.lines.single().glyphs[0].width, layout.lines.single().glyphs[1].width)
    }

    @Test
    fun `mixed variable width line uses actual glyph widths`() {
        val layout = createDotMatrixBlockLayout("LEDgram 최고 123", fakeProvider)
        val glyphs = layout.lines.single().glyphs
        val expectedWidth = glyphs.sumOf { dotMatrixGlyphWidthUnits(it).toDouble() }.toFloat() +
            (glyphs.size - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO

        assertTrue(glyphs.any { it.width == latinUpper.width })
        assertTrue(glyphs.any { it.width == latinLower.width })
        assertTrue(glyphs.any { it.width == hangul.width })
        assertEquals(expectedWidth, layout.widthUnits, 0.0001f)
    }

    @Test
    fun `Hangul Latin and Jamo share a stable baseline`() {
        val layout = createDotMatrixBlockLayout("a한ㄱ", fakeProvider)
        val line = layout.lines.single()

        assertEquals(dotMatrixGlyphBaselineUnits(hangul), line.baselineUnits, 0f)
        assertTrue(dotMatrixBaselineAlignmentOffsetUnits(line.baselineUnits, latinLower) > 0f)
        assertEquals(0f, dotMatrixBaselineAlignmentOffsetUnits(line.baselineUnits, hangul), 0f)
        assertEquals(0f, dotMatrixBaselineAlignmentOffsetUnits(line.baselineUnits, jamo), 0f)
    }

    @Test
    fun `compatibility and modern Jamo use provider glyph geometry`() {
        val layout = createDotMatrixBlockLayout("ㄱㄴㄷ ㅏㅑㅓ 가", fakeProvider)

        assertEquals(jamo.width, layout.lines.single().glyphs.first().width)
        assertTrue(layout.lines.single().glyphs.count { it.width == jamo.width } >= 8)
    }

    @Test
    fun `supplementary Unicode requests one fallback glyph`() {
        val requestedCodePoints = mutableListOf<Int>()
        val layout = createDotMatrixBlockLayout("😀", glyphProvider = { codePoint ->
            requestedCodePoints += codePoint
            fakeProvider(codePoint)
        })

        assertEquals(listOf(0x1F600), requestedCodePoints)
        assertEquals(1, layout.lines.single().glyphs.size)
        assertEquals(FALLBACK_GLYPH, layout.lines.single().glyphs.single())
    }

    @Test
    fun `multiline keeps empty lines and widest logical line`() {
        val layout = createDotMatrixBlockLayout("안녕하세요\n\nLEDgram", fakeProvider)

        assertEquals(3, layout.lines.size)
        assertEquals(layout.lines.maxOf { it.widthUnits }, layout.widthUnits, 0f)
        assertTrue(layout.lines[1].glyphs.isEmpty())
        assertTrue(layout.lines[1].heightUnits > 0f)
    }

    @Test
    fun `bold stays one larger dot with symmetric geometry`() {
        val normal = createDotMatrixBlockLayout("한글", fakeProvider)
        val bold = createDotMatrixBlockLayout("한글", fakeProvider, bold = true)

        assertEquals(
            normal.widthUnits + DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f,
            bold.widthUnits,
            0.0001f,
        )
        assertEquals(DOT_MATRIX_BOLD_RADIUS_RATIO, dotMatrixDotRadiusUnits(true), 0f)
        assertTrue(DOT_MATRIX_BOLD_RADIUS_RATIO * 2f < 1f + DOT_MATRIX_DOT_GAP_RATIO)
    }

    @Test
    fun `italic slant scales over each glyph row count`() {
        listOf(latinLower, hangul, jamo).forEach { glyph ->
            assertEquals(DOT_MATRIX_ITALIC_OVERSCAN_RATIO, dotMatrixItalicOffsetUnits(glyph, 0), 0f)
            assertEquals(0f, dotMatrixItalicOffsetUnits(glyph, glyph.height - 1), 0f)
        }
    }

    @Test
    fun `underline uses full mixed line geometry`() {
        val normal = createDotMatrixBlockLayout("abc 한글", fakeProvider)
        val underlined = createDotMatrixBlockLayout("abc 한글", fakeProvider, underline = true)

        assertTrue(underlined.lines.single().underlined)
        assertEquals(normal.widthUnits, underlined.widthUnits, 0f)
        assertEquals(
            normal.heightUnits + DOT_MATRIX_UNDERLINE_SPACING_RATIO +
                DOT_MATRIX_UNDERLINE_HEIGHT_RATIO,
            underlined.heightUnits,
            0.0001f,
        )
    }

    @Test
    fun `static fitting accounts for mixed styled bounds`() {
        val layout = createDotMatrixBlockLayout(
            "AbCd 한글\nㄱㄴㄷ 123",
            fakeProvider,
            bold = true,
            italic = true,
            underline = true,
        )
        val dotSize = calculateFittingDotSize(layout, 320f, 180f, fitWidth = true)

        assertTrue(dotSize > 0f)
        assertTrue(layout.widthUnits * dotSize <= 320.0001f)
        assertTrue(layout.heightUnits * dotSize <= 180.0001f)
    }

    @Test
    fun `marquee uses full mixed block width and height fitting`() {
        val layout = createDotMatrixBlockLayout("Hello 안녕\nGO!", fakeProvider)
        val dotSize = calculateFittingDotSize(layout, 1f, 180f, fitWidth = false)
        val blockWidth = layout.widthUnits * dotSize
        val travel = calculateMarqueeTravelBounds(1_000f, blockWidth, MotionEffect.RIGHT_TO_LEFT)

        assertTrue(dotSize > 0f)
        assertEquals(1_000f, travel.startPx, 0f)
        assertEquals(-blockWidth, travel.endPx, 0f)
    }

    @Test
    fun `larger viewport allows larger fitted dots`() {
        val layout = createDotMatrixBlockLayout("Hello 안녕", fakeProvider)
        val small = calculateFittingDotSize(layout, 200f, 100f, fitWidth = true)
        val large = calculateFittingDotSize(layout, 400f, 200f, fitWidth = true)

        assertEquals(small * 2f, large, 0.0001f)
    }

    private fun solidGlyph(width: Int, height: Int, baseline: Int): DotMatrixGlyph =
        DotMatrixGlyph(
            width = width,
            height = height,
            rows = List(height) { (1 shl width) - 1 },
            baseline = baseline,
        )
}
