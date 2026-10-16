package com.gwolbisa.ledgram.util

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

fun ComponentActivity.enableLedgramEdgeToEdge() {
    val darkSurfaceStyle = SystemBarStyle.dark(Color.TRANSPARENT)
    enableEdgeToEdge(
        statusBarStyle = darkSurfaceStyle,
        navigationBarStyle = darkSurfaceStyle,
    )
}
