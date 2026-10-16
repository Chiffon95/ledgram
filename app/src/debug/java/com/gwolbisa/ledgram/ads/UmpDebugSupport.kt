package com.gwolbisa.ledgram.ads

import android.content.Context
import com.gwolbisa.ledgram.BuildConfig
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import java.util.concurrent.atomic.AtomicBoolean

internal object UmpDebugSupport {
    private val resetApplied = AtomicBoolean(false)

    fun applyBeforeConsentInfoUpdate(
        context: Context,
        consentInformation: ConsentInformation,
        requestParametersBuilder: ConsentRequestParameters.Builder,
    ) {
        if (BuildConfig.UMP_RESET_CONSENT_ON_LAUNCH && resetApplied.compareAndSet(false, true)) {
            consentInformation.reset()
        }

        val debugGeography = when (BuildConfig.UMP_DEBUG_GEOGRAPHY) {
            "EEA" -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
            "US" -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_REGULATED_US_STATE
            else -> return
        }
        val testDeviceId = BuildConfig.UMP_TEST_DEVICE_ID
        if (testDeviceId.isBlank()) return

        val debugSettings = ConsentDebugSettings.Builder(context)
            .setDebugGeography(debugGeography)
            .addTestDeviceHashedId(testDeviceId)
            .build()
        requestParametersBuilder.setConsentDebugSettings(debugSettings)
    }
}
