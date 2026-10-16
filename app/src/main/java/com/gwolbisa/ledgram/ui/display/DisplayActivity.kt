package com.gwolbisa.ledgram.ui.display

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.gwolbisa.ledgram.BuildConfig
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.util.applyOrientationPolicy
import com.gwolbisa.ledgram.util.enableLedgramEdgeToEdge

class DisplayActivity : AppCompatActivity() {
    private var previousBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    private val completionTracker = DisplaySessionCompletionTracker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
        applyDisplayOrientationPolicy()
        enableLedgramEdgeToEdge()
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
        onBackPressedDispatcher.addCallback(this) { completeDisplaySession() }

        setContent {
            LEDgramTheme {
                DisplayScreen(
                    config = config,
                    onExit = ::completeDisplaySession,
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logLifecycle("onConfigurationChanged", newConfig)
        applyDisplayOrientationPolicy(newConfig)
    }

    override fun onDestroy() {
        logLifecycle("onDestroy")
        super.onDestroy()
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

    private fun completeDisplaySession() {
        completionTracker.completeOnce {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun applyDisplayOrientationPolicy(
        configuration: Configuration = resources.configuration,
    ) {
        applyOrientationPolicy(
            compactOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            configuration = configuration,
        )
    }

    private fun logLifecycle(
        event: String,
        configuration: Configuration = resources.configuration,
    ) {
        if (!BuildConfig.DEBUG) return
        Log.d(
            LOG_TAG,
            "$event instance=${System.identityHashCode(this)} " +
                "orientation=${configuration.orientation} " +
                "widthDp=${configuration.screenWidthDp} " +
                "heightDp=${configuration.screenHeightDp}",
        )
    }

    companion object {
        const val EXTRA_DISPLAY_CONFIG = "com.gwolbisa.ledgram.extra.DISPLAY_CONFIG"
        private const val LOG_TAG = "LEDgramDisplay"
    }
}

