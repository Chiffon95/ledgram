package com.gwolbisa.ledgram.ads

import android.content.Context
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters

internal object UmpDebugSupport {
    fun applyBeforeConsentInfoUpdate(
        context: Context,
        consentInformation: ConsentInformation,
        requestParametersBuilder: ConsentRequestParameters.Builder,
    ) = Unit
}
