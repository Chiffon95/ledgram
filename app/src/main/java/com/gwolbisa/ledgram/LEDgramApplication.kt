package com.gwolbisa.ledgram

import android.app.Application
import com.gwolbisa.ledgram.ads.AdsManager
import com.gwolbisa.ledgram.ads.ConsentManager
import com.gwolbisa.ledgram.data.MonetizationPreferencesRepository
import com.gwolbisa.ledgram.ui.components.PixelFontGlyphProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LEDgramApplication : Application() {
    internal val dotMatrixGlyphProvider: PixelFontGlyphProvider by lazy {
        PixelFontGlyphProvider(this)
    }
    lateinit var consentManager: ConsentManager
        private set
    lateinit var adsManager: AdsManager
        private set
    lateinit var monetizationPreferencesRepository: MonetizationPreferencesRepository
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        consentManager = ConsentManager(this)
        adsManager = AdsManager(this)
        monetizationPreferencesRepository = MonetizationPreferencesRepository(this)
        applicationScope.launch {
            consentManager.canRequestAds.collectLatest(adsManager::setConsentAllowsAds)
        }
    }
}
