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
    fun `bold and italic add explicit horizontal geometry`() {
        val normal = createDotMatrixBlockLayout("LEDGRAM")
        val bold = createDotMatrixBlockLayout("LEDGRAM", bold = true)
        val italic = createDotMatrixBlockLayout("LEDGRAM", italic = true)
        val combined = createDotMatrixBlockLayout("LEDGRAM", bold = true, italic = true)

        assertEquals(
            normal.widthUnits + DOT_MATRIX_BOLD_OFFSET_RATIO,
            bold.widthUnits,
            0.0001f,
        )
        assertEquals(
            normal.widthUnits + DOT_MATRIX_ITALIC_OVERSCAN_RATIO,
            italic.widthUnits,
            0.0001f,
        )
        assertEquals(
            normal.widthUnits + DOT_MATRIX_BOLD_OFFSET_RATIO +
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
        assertEquals(underline.heightUnits, allStyles.heightUnits, 0.0001f)
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
        assertEquals(32.6f, layout.heightUnits, 0.0001f)
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
