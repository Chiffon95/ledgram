package com.gwolbisa.ledgram.ads

import com.gwolbisa.ledgram.data.MonetizationPreferences
import com.gwolbisa.ledgram.ui.display.DisplaySessionCompletionTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonetizationPolicyTest {
    private val now = 12L * 60L * 60L * 1_000L

    @Test
    fun `only five or more display exits pass the counter threshold`() {
        listOf(0, 1, 4).forEach { count ->
            assertFalse(
                isInterstitialEligible(
                    MonetizationPreferences(displayExitCount = count),
                    now,
                ),
            )
        }
        listOf(5, 6).forEach { count ->
            assertTrue(
                isInterstitialEligible(
                    MonetizationPreferences(displayExitCount = count),
                    now,
                ),
            )
        }
    }

    @Test
    fun `frequency cap allows zero or one recent show`() {
        assertTrue(isInterstitialEligible(eligiblePreferences(), now))
        assertTrue(
            isInterstitialEligible(
                eligiblePreferences(lastShown = now - 10 * 60 * 1_000L),
                now,
            ),
        )
    }

    @Test
    fun `frequency cap blocks two shows inside rolling window`() {
        assertFalse(
            isInterstitialEligible(
                eligiblePreferences(
                    lastShown = now - 10 * 60 * 1_000L,
                    previousShown = now - 20 * 60 * 1_000L,
                ),
                now,
            ),
        )
    }

    @Test
    fun `oldest show outside rolling window releases a slot`() {
        assertTrue(
            isInterstitialEligible(
                eligiblePreferences(
                    lastShown = now - 10 * 60 * 1_000L,
                    previousShown = now - 31 * 60 * 1_000L,
                ),
                now,
            ),
        )
    }

    @Test
    fun `show exactly thirty minutes old is outside the window`() {
        assertTrue(
            isInterstitialEligible(
                eligiblePreferences(
                    lastShown = now - 10 * 60 * 1_000L,
                    previousShown = now - INTERSTITIAL_FREQUENCY_WINDOW_MILLIS,
                ),
                now,
            ),
        )
    }

    @Test
    fun `only a successful show resets counter and rotates timestamps`() {
        val current = eligiblePreferences(lastShown = 100L, previousShown = 50L)
        val shown = applyInterstitialEvent(current, InterstitialEvent.SHOWN, 200L)

        assertEquals(0, shown.displayExitCount)
        assertEquals(200L, shown.lastInterstitialShownAtMillis)
        assertEquals(100L, shown.previousInterstitialShownAtMillis)
        assertEquals(current, applyInterstitialEvent(current, InterstitialEvent.LOAD_FAILED, 200L))
        assertEquals(current, applyInterstitialEvent(current, InterstitialEvent.SHOW_FAILED, 200L))
    }

    @Test
    fun `dismiss after successful show is a no-op`() {
        val shown = applyInterstitialEvent(
            eligiblePreferences(lastShown = 100L),
            InterstitialEvent.SHOWN,
            200L,
        )

        assertEquals(shown, applyInterstitialEvent(shown, InterstitialEvent.DISMISSED, 300L))
    }

    @Test
    fun `display completion tracker reports one user session exactly once`() {
        val tracker = DisplaySessionCompletionTracker()
        var completedCount = 0

        assertTrue(tracker.completeOnce { completedCount++ })
        assertFalse(tracker.completeOnce { completedCount++ })
        assertEquals(1, completedCount)
    }

    private fun eligiblePreferences(
        lastShown: Long = 0L,
        previousShown: Long = 0L,
    ) = MonetizationPreferences(
        displayExitCount = DISPLAY_EXITS_PER_INTERSTITIAL,
        lastInterstitialShownAtMillis = lastShown,
        previousInterstitialShownAtMillis = previousShown,
    )
}
