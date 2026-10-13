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
        val emoji = createDotMatrixBlockLayout("😀")
        val fallback = createDotMatrixBlockLayout("?")

        assertEquals(fallback.widthUnits, emoji.widthUnits, 0f)
        assertEquals(1, emoji.lines.single().glyphs.size)
    }

    @Test
    fun `larger viewport allows a larger fitted dot`() {
        val layout = createDotMatrixBlockLayout("HELLO\nWORLD")
        val small = calculateFittingDotSize(layout, 200f, 100f, fitWidth = true)
        val large = calculateFittingDotSize(layout, 400f, 200f, fitWidth = true)

        assertEquals(small * 2f, large, 0.0001f)
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
        val layout = createDotMatrixBlockLayout("LONG LINE\nSHORT")
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
