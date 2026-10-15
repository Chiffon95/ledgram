package com.gwolbisa.ledgram.ads

import androidx.annotation.StringRes
import com.gwolbisa.ledgram.R

internal object AdConfig {
    // Debug resource overlays resolve these to Google's official Android sample IDs.
    @StringRes
    val BANNER_ID = R.string.admob_banner_id

    @StringRes
    val INTERSTITIAL_ID = R.string.admob_interstitial_id
}
