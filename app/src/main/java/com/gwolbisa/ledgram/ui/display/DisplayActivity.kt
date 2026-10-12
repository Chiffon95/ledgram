package com.gwolbisa.ledgram.ui.display

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.IntentCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme

class DisplayActivity : ComponentActivity() {
    private var previousBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        previousBrightness = window.attributes.screenBrightness
        hideSystemBars()

        val config = IntentCompat.getParcelableExtra(
            intent,
            EXTRA_DISPLAY_CONFIG,
            DisplayConfig::class.java,
        ) ?: run {
            finish()
            return
        }

        setContent {
            LEDgramTheme {
                DisplayScreen(
                    config = config,
                    onExit = ::finish,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
        hideSystemBars()
    }

    override fun onPause() {
        window.attributes = window.attributes.apply {
            screenBrightness = previousBrightness
        }
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    companion object {
        const val EXTRA_DISPLAY_CONFIG = "com.gwolbisa.ledgram.extra.DISPLAY_CONFIG"
    }
}

