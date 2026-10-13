package com.gwolbisa.ledgram.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {
    @Test
    fun `white on black has sufficient contrast`() {
        assertFalse(isLowColorContrast(0xFFFFFFFF, 0xFF000000))
    }

    @Test
    fun `white on very light gray has low contrast`() {
        assertTrue(isLowColorContrast(0xFFFFFFFF, 0xFFF2F2F2))
    }
}
