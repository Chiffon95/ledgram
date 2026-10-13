package com.gwolbisa.ledgram.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TextFitTest {
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
    fun `complete static layout accepts one or two complete lines`() {
        assertTrue(
            isCompleteStaticTextLayout(
                textLength = 12,
                lineCount = 1,
                lastLineEnd = 12,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            isCompleteStaticTextLayout(
                textLength = 40,
                lineCount = 2,
                lastLineEnd = 40,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
    }

    @Test
    fun `static layout rejects clipping overflow and more than two lines`() {
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                lineCount = 2,
                lastLineEnd = 39,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                lineCount = 3,
                lastLineEnd = 40,
                didOverflowWidth = false,
                didOverflowHeight = false,
            ),
        )
        assertTrue(
            !isCompleteStaticTextLayout(
                textLength = 40,
                lineCount = 2,
                lastLineEnd = 40,
                didOverflowWidth = true,
                didOverflowHeight = false,
            ),
        )
    }
}
