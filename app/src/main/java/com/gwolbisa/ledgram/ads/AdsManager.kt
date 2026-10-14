package com.gwolbisa.ledgram.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdsManager(context: Context) {
    private val applicationContext = context.applicationContext
    private val initializationStarted = AtomicBoolean(false)
    private val _adsReady = MutableStateFlow(false)
    val adsReady: StateFlow<Boolean> = _adsReady.asStateFlow()
    private var consentAllowsAds = false
    private var mobileAdsInitialized = false
    private val interstitialController = InterstitialController(applicationContext) {
        consentAllowsAds && mobileAdsInitialized
    }

    fun setConsentAllowsAds(allowed: Boolean) {
        consentAllowsAds = allowed
        if (!allowed) {
            _adsReady.value = false
            interstitialController.clear()
            return
        }
        if (mobileAdsInitialized) {
            _adsReady.value = true
            interstitialController.preload()
        } else {
            initializeOnce()
        }
    }

    fun preloadInterstitial() {
        interstitialController.preload()
    }

    fun showInterstitialIfAvailable(
        activity: Activity,
        onShown: () -> Unit,
    ): Boolean = interstitialController.showIfAvailable(activity, onShown)

    private fun initializeOnce() {
        if (!initializationStarted.compareAndSet(false, true)) return
        try {
            MobileAds.initialize(applicationContext) {
                mobileAdsInitialized = true
                if (consentAllowsAds) {
                    _adsReady.value = true
                    interstitialController.preload()
                }
            }
        } catch (_: RuntimeException) {
            initializationStarted.set(false)
            _adsReady.value = false
        }
    }
}
