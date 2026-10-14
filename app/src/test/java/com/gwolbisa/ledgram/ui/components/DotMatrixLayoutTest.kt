package com.gwolbisa.ledgram.ui.components

import com.gwolbisa.ledgram.model.MotionEffect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixLayoutTest {
    @Test
    fun `single line block dimensions use glyph grid`() {
        val size = calculateDotMatrixBlockSize(message = "A", dotSize = 1f)

        assertEquals(6.2f, size.width, 0.0001f)
        assertEquals(8.8f, size.height, 0.0001f)
    }

    @Test
    fun `widest line controls multiline width and line count controls height`() {
        val layout = createDotMatrixBlockLayout("AAAA\nA\nAA")

        assertEquals(3, layout.lines.size)
        assertEquals(layout.lines[0].widthUnits, layout.widthUnits, 0.0001f)
        assertTrue(layout.lines[0].widthUnits > layout.lines[2].widthUnits)
        assertEquals(29f, layout.heightUnits, 0.0001f)
    }

    @Test
    fun `supplementary unicode uses one fallback glyph`() {
        val emoji = createDotMatrixBlockLayout("😀", bold = true, italic = true)
        val fallback = createDotMatrixBlockLayout("?", bold = true, italic = true)

        assertEquals(fallback.widthUnits, emoji.widthUnits, 0f)
        assertEquals(1, emoji.lines.single().glyphs.size)
    }

    @Test
    fun `mixed Korean English and digits use actual glyph widths`() {
        val layout = createDotMatrixBlockLayout("LEDgram 최고 123")
        val glyphs = layout.lines.single().glyphs
        val expectedContentWidth = glyphs.sumOf {
            dotMatrixGlyphWidthUnits(it).toDouble()
        }.toFloat() + (glyphs.size - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO

        assertTrue(glyphs.any { it.width == DOT_MATRIX_GLYPH_WIDTH })
        assertTrue(glyphs.any { it.width == DOT_MATRIX_HANGUL_GLYPH_WIDTH })
        assertEquals(expectedContentWidth, layout.lines.single().contentWidthUnits, 0.0001f)
        assertEquals(expectedContentWidth, layout.widthUnits, 0.0001f)
    }

    @Test
    fun `mixed line uses tallest glyph and centers shorter glyph geometry`() {
        val layout = createDotMatrixBlockLayout("GO 한국")
        val line = layout.lines.single()
        val latin = dotMatrixGlyphFor('G')
        val hangul = dotMatrixGlyphFor('한')

        assertEquals(
            dotMatrixGlyphHeightUnits(hangul),
            line.contentHeightUnits,
            0.0001f,
        )
        assertTrue(line.contentHeightUnits > dotMatrixGlyphHeightUnits(latin))
        assertTrue(dotMatrixVerticalAlignmentOffsetUnits(line.contentHeightUnits, latin) > 0f)
        assertEquals(0f, dotMatrixVerticalAlignmentOffsetUnits(line.contentHeightUnits, hangul), 0f)
    }

    @Test
    fun `italic slant scales over each glyph row count`() {
        listOf(dotMatrixGlyphFor('A'), dotMatrixGlyphFor('한')).forEach { glyph ->
            assertEquals(
                DOT_MATRIX_ITALIC_OVERSCAN_RATIO,
                dotMatrixItalicOffsetUnits(glyph, 0),
                0f,
            )
            assertEquals(0f, dotMatrixItalicOffsetUnits(glyph, glyph.height - 1), 0f)
        }
    }

    @Test
    fun `multiline Hangul centers logical lines in widest block`() {
        val layout = createDotMatrixBlockLayout("안녕하세요\nLEDgram")

        assertEquals(2, layout.lines.size)
        assertEquals(layout.lines.maxOf { it.widthUnits }, layout.widthUnits, 0.0001f)
        assertTrue(layout.heightUnits > layout.lines.sumOf { it.contentHeightUnits.toDouble() })
    }

    @Test
    fun `Hangul styles include bold italic and underline geometry`() {
        val normal = createDotMatrixBlockLayout("응원\nGO GO")
        val styled = createDotMatrixBlockLayout("응원\nGO GO", bold = true, italic = true, underline = true)

        assertEquals(DOT_MATRIX_BOLD_RADIUS_RATIO, dotMatrixDotRadiusUnits(styled.bold), 0f)
        assertTrue(styled.widthUnits > normal.widthUnits)
        assertTrue(styled.heightUnits > normal.heightUnits)
        assertTrue(styled.lines.all { it.underlined })
        assertTrue(calculateFittingDotSize(styled, 320f, 180f, fitWidth = true) > 0f)
        assertTrue(calculateFittingDotSize(styled, 0f, 180f, fitWidth = false) > 0f)
    }

    @Test
    fun `bold single-dot radius adds symmetric geometry and preserves spacing`() {
        val normal = createDotMatrixBlockLayout("LEDGRAM")
        val bold = createDotMatrixBlockLayout("LEDGRAM", bold = true)
        val italic = createDotMatrixBlockLayout("LEDGRAM", italic = true)
        val combined = createDotMatrixBlockLayout("LEDGRAM", bold = true, italic = true)

        assertEquals(
            normal.widthUnits + DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f,
            bold.widthUnits,
            0.0001f,
        )
        assertEquals(
            normal.heightUnits + DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f,
            bold.heightUnits,
            0.0001f,
        )
        assertEquals(DOT_MATRIX_BOLD_RADIUS_RATIO, dotMatrixDotRadiusUnits(true), 0f)
        assertEquals(DOT_MATRIX_NORMAL_RADIUS_RATIO, dotMatrixDotRadiusUnits(false), 0f)
        assertTrue(DOT_MATRIX_BOLD_RADIUS_RATIO * 2f < 1f + DOT_MATRIX_DOT_GAP_RATIO)
        assertEquals(
            normal.widthUnits + DOT_MATRIX_ITALIC_OVERSCAN_RATIO,
            italic.widthUnits,
            0.0001f,
        )
        assertEquals(
            normal.widthUnits + DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f +
                DOT_MATRIX_ITALIC_OVERSCAN_RATIO,
            combined.widthUnits,
            0.0001f,
        )
    }

    @Test
    fun `underline increases non-empty line height and combines with other styles`() {
        val normal = createDotMatrixBlockLayout("A")
        val underline = createDotMatrixBlockLayout("A", underline = true)
        val allStyles = createDotMatrixBlockLayout(
            "A",
            bold = true,
            italic = true,
            underline = true,
        )

        assertEquals(
            normal.heightUnits + DOT_MATRIX_UNDERLINE_SPACING_RATIO +
                DOT_MATRIX_UNDERLINE_HEIGHT_RATIO,
            underline.heightUnits,
            0.0001f,
        )
        assertTrue(allStyles.widthUnits > normal.widthUnits)
        assertEquals(
            normal.heightUnits + DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f +
                DOT_MATRIX_UNDERLINE_SPACING_RATIO + DOT_MATRIX_BOLD_RADIUS_RATIO * 2f,
            allStyles.heightUnits,
            0.0001f,
        )
    }

    @Test
    fun `multiline underline skips empty lines and widest styled line controls width`() {
        val layout = createDotMatrixBlockLayout(
            "HELLO\n\nWORLDWIDE",
            bold = true,
            italic = true,
            underline = true,
        )

        assertTrue(layout.lines[0].underlined)
        assertTrue(!layout.lines[1].underlined)
        assertTrue(layout.lines[2].underlined)
        assertEquals(layout.lines[2].widthUnits, layout.widthUnits, 0.0001f)
        val styledLineHeight = DOT_MATRIX_GLYPH_HEIGHT_UNITS +
            DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f +
            DOT_MATRIX_UNDERLINE_SPACING_RATIO + DOT_MATRIX_BOLD_RADIUS_RATIO * 2f
        assertEquals(
            styledLineHeight * 2f + DOT_MATRIX_GLYPH_HEIGHT_UNITS +
                DOT_MATRIX_LINE_GAP_RATIO * 2f,
            layout.heightUnits,
            0.0001f,
        )
    }

    @Test
    fun `larger viewport allows a larger fitted dot`() {
        val layout = createDotMatrixBlockLayout("HELLO\nWORLD")
        val small = calculateFittingDotSize(layout, 200f, 100f, fitWidth = true)
        val large = calculateFittingDotSize(layout, 400f, 200f, fitWidth = true)

        assertEquals(small * 2f, large, 0.0001f)
    }

    @Test
    fun `static fitting accounts for combined styled bounds`() {
        val normal = createDotMatrixBlockLayout("HELLO\nWORLD")
        val styled = createDotMatrixBlockLayout(
            "HELLO\nWORLD",
            bold = true,
            italic = true,
            underline = true,
        )

        val normalDotSize = calculateFittingDotSize(normal, 200f, 100f, fitWidth = true)
        val styledDotSize = calculateFittingDotSize(styled, 200f, 100f, fitWidth = true)

        assertTrue(styledDotSize < normalDotSize)
    }

    @Test
    fun `every style combination has valid multiline static and marquee geometry`() {
        val combinations = listOf(
            Triple(false, false, false),
            Triple(true, false, false),
            Triple(false, true, false),
            Triple(false, false, true),
            Triple(true, true, false),
            Triple(true, false, true),
            Triple(false, true, true),
            Triple(true, true, true),
        )

        combinations.forEach { (bold, italic, underline) ->
            val layout = createDotMatrixBlockLayout(
                message = "LED\nGRAM",
                bold = bold,
                italic = italic,
                underline = underline,
            )
            val staticDotSize = calculateFittingDotSize(
                layout = layout,
                availableWidth = 320f,
                availableHeight = 180f,
                fitWidth = true,
            )
            val marqueeDotSize = calculateFittingDotSize(
                layout = layout,
                availableWidth = 0f,
                availableHeight = 180f,
                fitWidth = false,
            )

            assertTrue(staticDotSize > 0f)
            assertTrue(marqueeDotSize > 0f)
            assertTrue(layout.widthUnits * staticDotSize <= 320.0001f)
            assertTrue(layout.heightUnits * staticDotSize <= 180.0001f)
            assertTrue(layout.heightUnits * marqueeDotSize <= 180.0001f)
            assertTrue(layout.widthUnits * marqueeDotSize > 0f)
        }
    }

    @Test
    fun `marquee fit ignores width and shrinks for additional lines`() {
        val oneLine = createDotMatrixBlockLayout("HELLO")
        val threeLines = createDotMatrixBlockLayout("HELLO\nWORLD\nLEDGRAM")
        val narrow = calculateFittingDotSize(oneLine, 1f, 100f, fitWidth = false)
        val wide = calculateFittingDotSize(oneLine, 10_000f, 100f, fitWidth = false)
        val multiline = calculateFittingDotSize(threeLines, 10_000f, 100f, fitWidth = false)

        assertEquals(narrow, wide, 0f)
        assertTrue(multiline < wide)
    }

    @Test
    fun `marquee travel uses stable unscaled block width`() {
        val layout = createDotMatrixBlockLayout(
            "LONG LINE\nSHORT",
            bold = true,
            italic = true,
            underline = true,
        )
        val dotSize = calculateFittingDotSize(layout, 0f, 200f, fitWidth = false)
        val blockWidth = layout.widthUnits * dotSize
        val rightToLeft = calculateMarqueeTravelBounds(1_000f, blockWidth, MotionEffect.RIGHT_TO_LEFT)
        val leftToRight = calculateMarqueeTravelBounds(1_000f, blockWidth, MotionEffect.LEFT_TO_RIGHT)

        assertEquals(1_000f, rightToLeft.startPx, 0f)
        assertEquals(-blockWidth, rightToLeft.endPx, 0f)
        assertEquals(-blockWidth, leftToRight.startPx, 0f)
        assertEquals(1_000f, leftToRight.endPx, 0f)
    }
}
