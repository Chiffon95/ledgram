package com.gwolbisa.ledgram.ads

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConsentManager(context: Context) {
    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(context.applicationContext)

    private val _canRequestAds = MutableStateFlow(consentInformation.canRequestAds())
    val canRequestAds: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(isPrivacyOptionsRequired())
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    private var updateInProgress = false

    fun requestConsentInfoUpdate(activity: Activity) {
        if (updateInProgress) return
        updateInProgress = true
        val requestParameters = ConsentRequestParameters.Builder().build()
        try {
            consentInformation.requestConsentInfoUpdate(
                activity,
                requestParameters,
                {
                    refreshState()
                    try {
                        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                            updateInProgress = false
                            refreshState()
                        }
                    } catch (_: RuntimeException) {
                        updateInProgress = false
                        refreshState()
                    }
                },
                {
                    updateInProgress = false
                    refreshState()
                },
            )
        } catch (_: RuntimeException) {
            updateInProgress = false
            refreshState()
        }
    }

    fun showPrivacyOptionsForm(activity: Activity) {
        if (!isPrivacyOptionsRequired()) {
            refreshState()
            return
        }
        try {
            UserMessagingPlatform.showPrivacyOptionsForm(activity) {
                refreshState()
            }
        } catch (_: RuntimeException) {
            refreshState()
        }
    }

    private fun refreshState() {
        _canRequestAds.value = consentInformation.canRequestAds()
        _privacyOptionsRequired.value = isPrivacyOptionsRequired()
    }

    private fun isPrivacyOptionsRequired(): Boolean =
        consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
}
