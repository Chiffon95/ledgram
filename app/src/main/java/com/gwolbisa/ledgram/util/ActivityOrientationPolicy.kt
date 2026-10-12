package com.gwolbisa.ledgram.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration

private const val LARGE_SCREEN_MIN_WIDTH_DP = 600

fun Activity.applyOrientationPolicy(
    compactOrientation: Int,
    configuration: Configuration = resources.configuration,
) {
    val preferredOrientation = if (
        configuration.smallestScreenWidthDp < LARGE_SCREEN_MIN_WIDTH_DP
    ) {
        compactOrientation
    } else {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    if (requestedOrientation != preferredOrientation) {
        requestedOrientation = preferredOrientation
    }
}
