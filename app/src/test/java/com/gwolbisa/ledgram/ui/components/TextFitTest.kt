package com.gwolbisa.ledgram.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFitTest {
    @Test
    fun `spaces do not create implicit logical lines`() {
        assertEquals(listOf("A B C"), splitExplicitTextLines("A B C"))
    }

    @Test
    fun `only explicit newlines create logical lines`() {
        assertEquals(listOf("A B", "C D"), splitExplicitTextLines("A B\nC D"))
    }

    @Test
    fun `long logical line shrinks without becoming multiple lines`() {
        val text = "A VERY LONG SINGLE LOGICAL LINE"
        val fitted = findLargestFittingFontSize(maximumFontSizeSp = 100f) { candidate ->
            candidate * text.length <= 300f
        }

        assertEquals(1, splitExplicitTextLines(text).size)
        assertTrue(fitted < 100f)
        assertTrue(fitted * text.length <= 300.01f)
    }

    @Test
    fun `search returns the largest fitting size`() {
        val result = findLargestFittingFontSize(maximumFontSizeSp = 100f) { it <= 42f }

        assertTrue(result <= 42f)
        assertTrue(result > 41.99f)
    }

    @Test
    fun `search keeps the requested maximum when it fits`() {
        val result = findLargestFittingFontSize(maximumFontSizeSp = 64f) { true }

        assertTrue(result <= 64f)
        assertTrue(result > 63.99f)
    }

    @Test
    fun `search uses technical floor when no size fits`() {
        assertEquals(
            0.01f,
            findLargestFittingFontSize(maximumFontSizeSp = 64f) { false },
            0f,
        )
    }

    @Test
    fun `search can shrink below one sp for exceptionally long text`() {
        val result = findLargestFittingFontSize(maximumFontSizeSp = 64f) { it <= 0.5f }

        assertTrue(result <= 0.5f)
        assertTrue(result > 0.49f)
    }

    @Test
    fun `complete static layout accepts arbitrary complete line counts`() {
        assertTrue(
            isCompleteStaticTextLayout(
                textLength = 12,
                expectedLineCount = 1,
                lineCount = 1,
                lastLineEnd = 12,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            isCompleteStaticTextLayout(
                textLength = 100,
                expectedLineCount = 50,
                lineCount = 50,
                lastLineEnd = 100,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
    }

    @Test
    fun `static layout rejects incomplete and overflowing text`() {
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                expectedLineCount = 2,
                lineCount = 2,
                lastLineEnd = 39,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                expectedLineCount = 2,
                lineCount = 2,
                lastLineEnd = 40,
                didOverflowWidth = true,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                expectedLineCount = 4,
                lineCount = 4,
                lastLineEnd = 40,
                didOverflowWidth = false,
                didOverflowHeight = true,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 0,
                expectedLineCount = 1,
                lineCount = 0,
                lastLineEnd = 0,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                expectedLineCount = 1,
                lineCount = 2,
                lastLineEnd = 40,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
    }

    @Test
    fun `marquee height fitting reduces size as explicit line count increases`() {
        fun fittedSize(lineCount: Int): Float = findLargestFittingFontSize(100f) { candidate ->
            candidate * lineCount <= 100f
        }

        val sizes = listOf(1, 2, 3, 5).map(::fittedSize)

        assertEquals(100f, sizes[0], 0.01f)
        assertEquals(50f, sizes[1], 0.01f)
        assertEquals(100f / 3f, sizes[2], 0.01f)
        assertEquals(20f, sizes[3], 0.01f)
        assertTrue(sizes.zipWithNext().all { (previous, next) -> next < previous })
    }

    @Test
    fun `complete marquee layout accepts arbitrary explicit line counts`() {
        listOf(1, 2, 3, 5).forEach { lineCount ->
            assertTrue(
                isCompleteMarqueeTextLayout(
                    textLength = 100,
                    expectedLineCount = lineCount,
                    lineCount = lineCount,
                    lastLineEnd = 100,
                    didOverflowHeight = false,
                ),
            )
        }
    }

    @Test
    fun `marquee layout rejects truncated or vertically overflowing blocks`() {
        assertTrue(
            !isCompleteMarqueeTextLayout(
                textLength = 100,
                expectedLineCount = 5,
                lineCount = 5,
                lastLineEnd = 99,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteMarqueeTextLayout(
                textLength = 100,
                expectedLineCount = 5,
                lineCount = 5,
                lastLineEnd = 100,
                didOverflowHeight = true,
            ),
        )
    }
}
