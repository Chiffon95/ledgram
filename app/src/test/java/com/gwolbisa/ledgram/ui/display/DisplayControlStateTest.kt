package com.gwolbisa.ledgram.ui.display

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayControlStateTest {
    @Test
    fun `new display session is unlocked and can exit`() {
        val state = DisplayControlState()

        assertFalse(state.isLocked)
        assertTrue(state.canExit)
    }

    @Test
    fun `lock blocks exit and keeps unlock control visible`() {
        val state = DisplayControlState().lock()

        assertTrue(state.isLocked)
        assertFalse(state.canExit)
        assertTrue(state.isOverlayVisible)
    }

    @Test
    fun `unlock restores exit and resets overlay timeout`() {
        val locked = DisplayControlState().lock()
        val unlocked = locked.unlock()

        assertFalse(unlocked.isLocked)
        assertTrue(unlocked.canExit)
        assertTrue(unlocked.isOverlayVisible)
        assertTrue(unlocked.overlayRevision > locked.overlayRevision)
    }

    @Test
    fun `revealing locked controls never unlocks display`() {
        val hiddenLocked = DisplayControlState().lock().hideOverlay()
        val revealed = hiddenLocked.revealOverlay()

        assertTrue(revealed.isLocked)
        assertFalse(revealed.canExit)
        assertTrue(revealed.isOverlayVisible)
    }
}
