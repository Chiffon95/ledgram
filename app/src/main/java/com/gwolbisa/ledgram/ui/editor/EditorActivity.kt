package com.gwolbisa.ledgram.ui.editor

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.gwolbisa.ledgram.BuildConfig
import com.gwolbisa.ledgram.LEDgramApplication
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.ads.InterstitialPreloadCoordinator
import com.gwolbisa.ledgram.ads.isInterstitialEligible
import com.gwolbisa.ledgram.data.EditorPreferencesRepository
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.ui.display.DisplayActivity
import com.gwolbisa.ledgram.ui.settings.SettingsActivity
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.util.applyOrientationPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditorActivity : AppCompatActivity() {
    @Volatile
    private var initialEditorLayoutReady = false
    @Volatile
    private var initialDotMatrixPreviewPrepared = false
    private var interstitialPreloadRequest by mutableIntStateOf(0)
    private val editorViewModel: EditorViewModel by viewModels {
        EditorViewModel.Factory(EditorPreferencesRepository(this))
    }
    private val displayLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) recordCompletedDisplaySession()
    }
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK &&
            result.data?.getBooleanExtra(SettingsActivity.EXTRA_EDITOR_SETTINGS_RESET, false) == true
        ) {
            editorViewModel.applyCanonicalDefaultsAfterReset()
        }
    }

    private val ledgramApplication: LEDgramApplication
        get() = application as LEDgramApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
        val previewPlaceholder = getString(R.string.message_placeholder)
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                val initialState = editorViewModel.uiState.filterNotNull().first()
                if (initialState.renderStyle == RenderStyle.DOT_MATRIX) {
                    val previewMessage = initialState.toPreviewConfig(previewPlaceholder).message
                    ledgramApplication.dotMatrixGlyphProvider.prepareGlyphs(previewMessage)
                }
            } finally {
                // Never leave the splash pinned if the verified bundled font cannot be prepared.
                initialDotMatrixPreviewPrepared = true
            }
        }
        splashScreen.setKeepOnScreenCondition {
            val state = editorViewModel.uiState.value
            state == null ||
                !initialEditorLayoutReady ||
                state.renderStyle == RenderStyle.DOT_MATRIX && !initialDotMatrixPreviewPrepared
        }
        applyEditorOrientationPolicy()
        enableEdgeToEdge()
        ledgramApplication.consentManager.requestConsentInfoUpdate(this)

        setContent {
            // Locale/layout-direction changes are handled in place and must invalidate resources.
            LocalConfiguration.current
            LEDgramTheme {
                val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()
                val adsReady by ledgramApplication.adsManager.adsReady.collectAsStateWithLifecycle()
                LaunchedEffect(interstitialPreloadRequest) {
                    if (interstitialPreloadRequest > 0) {
                        // Let the resumed Editor commit a frame before starting preload work.
                        withFrameNanos { }
                        ledgramApplication.adsManager.preloadInterstitial()
                    }
                }
                uiState?.let { state ->
                    EditorScreen(
                        uiState = state,
                        onUiStateChange = editorViewModel::setUiState,
                        onStartDisplay = ::openDisplay,
                        onOpenSettings = ::openSettings,
                        showBanner = adsReady,
                        modifier = Modifier.onGloballyPositioned {
                            initialEditorLayoutReady = true
                        },
                    )
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logLifecycle("onConfigurationChanged", newConfig)
        applyEditorOrientationPolicy(newConfig)
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        logLifecycle("onPause")
        super.onPause()
    }

    override fun onStop() {
        logLifecycle("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        logLifecycle("onDestroy")
        super.onDestroy()
    }

    private fun applyEditorOrientationPolicy(
        configuration: Configuration = resources.configuration,
    ) {
        applyOrientationPolicy(
            compactOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            configuration = configuration,
        )
    }

    private fun openDisplay(config: DisplayConfig) {
        displayLauncher.launch(
            Intent(this, DisplayActivity::class.java)
                .putExtra(DisplayActivity.EXTRA_DISPLAY_CONFIG, config),
        )
    }

    private fun openSettings() {
        settingsLauncher.launch(Intent(this, SettingsActivity::class.java))
    }

    private fun recordCompletedDisplaySession() {
        lifecycleScope.launch {
            val repository = ledgramApplication.monetizationPreferencesRepository
            val preferences = repository.recordDisplayCompletion()
            val nowMillis = System.currentTimeMillis()
            val preloadCoordinator = InterstitialPreloadCoordinator(
                requestPreload = ::requestInterstitialPreloadAfterFrame,
            )
            if (isInterstitialEligible(preferences, nowMillis)) {
                val adStarted = ledgramApplication.adsManager.showInterstitialIfAvailable(
                    activity = this@EditorActivity,
                    onShown = {
                        lifecycleScope.launch {
                            repository.recordInterstitialShown(System.currentTimeMillis())
                        }
                    },
                    onFinished = preloadCoordinator::onFinished,
                )
                preloadCoordinator.onShowResult(adStarted)
            } else {
                preloadCoordinator.onShowResult(adStarted = false)
            }
        }
    }

    private fun requestInterstitialPreloadAfterFrame() {
        interstitialPreloadRequest = if (interstitialPreloadRequest == Int.MAX_VALUE) {
            1
        } else {
            interstitialPreloadRequest + 1
        }
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

    private companion object {
        const val LOG_TAG = "LEDgramEditor"
    }
}

