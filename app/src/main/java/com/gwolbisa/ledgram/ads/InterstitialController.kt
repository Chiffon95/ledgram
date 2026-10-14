package com.gwolbisa.ledgram.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

internal class InterstitialController(
    context: Context,
    private val canRequestAds: () -> Boolean,
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val applicationContext = context.applicationContext
    private var interstitialAd: InterstitialAd? = null
    private var loadInProgress = false
    private var showing = false
    private var retryScheduled = false

    fun preload() {
        if (!canRequestAds() || loadInProgress || showing || interstitialAd != null) return
        loadInProgress = true
        try {
            InterstitialAd.load(
                applicationContext,
                applicationContext.getString(AdConfig.TEST_INTERSTITIAL_ID),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        loadInProgress = false
                        if (canRequestAds()) {
                            interstitialAd = ad
                        }
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        loadInProgress = false
                        interstitialAd = null
                        scheduleRetry()
                    }
                },
            )
        } catch (_: RuntimeException) {
            loadInProgress = false
            interstitialAd = null
            scheduleRetry()
        }
    }

    fun showIfAvailable(
        activity: Activity,
        onShown: () -> Unit,
        onFinished: () -> Unit,
    ): Boolean {
        if (!canRequestAds() || showing) return false
        val ad = interstitialAd ?: return false
        val shownOnce = OneShotAction(onShown)
        val finishedOnce = OneShotAction(onFinished)
        interstitialAd = null
        showing = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                shownOnce.invoke()
            }

            override fun onAdDismissedFullScreenContent() {
                showing = false
                finishedOnce.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                showing = false
                finishedOnce.invoke()
            }
        }
        return try {
            ad.show(activity)
            true
        } catch (_: RuntimeException) {
            showing = false
            finishedOnce.invoke()
            false
        }
    }

    fun clear() {
        interstitialAd = null
    }

    private fun scheduleRetry() {
        if (retryScheduled || !canRequestAds()) return
        retryScheduled = true
        mainHandler.postDelayed(
            {
                retryScheduled = false
                preload()
            },
            LOAD_RETRY_DELAY_MILLIS,
        )
    }

    private companion object {
        const val LOAD_RETRY_DELAY_MILLIS = 60_000L
    }
}
