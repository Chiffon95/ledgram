package com.gwolbisa.ledgram.ui.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorFooterAdLayoutTest {
    @Test
    fun `ads readiness changes content without changing reserved footer height`() {
        val initializing = editorFooterAdLayout(
            adsReady = false,
            isKeyboardVisible = false,
        )
        val ready = editorFooterAdLayout(
            adsReady = true,
            isKeyboardVisible = false,
        )

        assertTrue(initializing.footerVisible)
        assertTrue(ready.footerVisible)
        assertEquals(50, initializing.reservedBannerHeightDp)
        assertEquals(initializing.reservedBannerHeightDp, ready.reservedBannerHeightDp)
        assertFalse(initializing.showBannerContent)
        assertTrue(ready.showBannerContent)
    }

    @Test
    fun `keyboard hides footer and does not leave a banner gap`() {
        val layout = editorFooterAdLayout(
            adsReady = true,
            isKeyboardVisible = true,
        )

        assertFalse(layout.footerVisible)
        assertEquals(0, layout.reservedBannerHeightDp)
        assertFalse(layout.showBannerContent)
    }
}
