package com.gwolbisa.ledgram.ads

import org.junit.Assert.assertEquals
import org.junit.Test

class InterstitialPreloadCoordinatorTest {
    @Test
    fun `cache miss requests preload once`() {
        var requests = 0
        val coordinator = InterstitialPreloadCoordinator { requests++ }

        coordinator.onShowResult(adStarted = false)

        assertEquals(1, requests)
    }

    @Test
    fun `started ad waits for terminal callback before preload`() {
        var requests = 0
        val coordinator = InterstitialPreloadCoordinator { requests++ }

        coordinator.onShowResult(adStarted = true)
        assertEquals(0, requests)

        coordinator.onFinished()
        assertEquals(1, requests)
    }

    @Test
    fun `synchronous show failure requests preload exactly once`() {
        var requests = 0
        val coordinator = InterstitialPreloadCoordinator { requests++ }

        coordinator.onFinished()
        coordinator.onShowResult(adStarted = false)

        assertEquals(1, requests)
    }

    @Test
    fun `duplicate terminal callbacks remain idempotent`() {
        var requests = 0
        val coordinator = InterstitialPreloadCoordinator { requests++ }

        coordinator.onShowResult(adStarted = true)
        coordinator.onFinished()
        coordinator.onFinished()

        assertEquals(1, requests)
    }

    @Test
    fun `one shot action invokes its callback once`() {
        var invocations = 0
        val action = OneShotAction { invocations++ }

        action.invoke()
        action.invoke()

        assertEquals(1, invocations)
    }
}
