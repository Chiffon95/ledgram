package com.gwolbisa.ledgram.ads

import java.util.concurrent.atomic.AtomicBoolean

internal class OneShotAction(
    private val action: () -> Unit,
) {
    private val invoked = AtomicBoolean(false)

    fun invoke() {
        if (invoked.compareAndSet(false, true)) action()
    }
}

internal class InterstitialPreloadCoordinator(
    requestPreload: () -> Unit,
) {
    private val requestPreloadOnce = OneShotAction(requestPreload)

    fun onShowResult(adStarted: Boolean) {
        if (!adStarted) requestPreloadOnce.invoke()
    }

    fun onFinished() {
        requestPreloadOnce.invoke()
    }
}
