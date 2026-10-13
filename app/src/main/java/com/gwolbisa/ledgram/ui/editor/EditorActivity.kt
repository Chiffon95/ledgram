package com.gwolbisa.ledgram.ui.editor

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gwolbisa.ledgram.data.EditorPreferencesRepository
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.display.DisplayActivity
import com.gwolbisa.ledgram.ui.settings.SettingsActivity
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.util.applyOrientationPolicy

class EditorActivity : ComponentActivity() {
    private val editorViewModel: EditorViewModel by viewModels {
        EditorViewModel.Factory(EditorPreferencesRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        applyEditorOrientationPolicy()
        enableEdgeToEdge()

        setContent {
            LEDgramTheme {
                val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()
                uiState?.let { state ->
                    EditorScreen(
                        uiState = state,
                        onUiStateChange = editorViewModel::setUiState,
                        onStartDisplay = ::openDisplay,
                        onOpenSettings = ::openSettings,
                    )
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyEditorOrientationPolicy(newConfig)
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
        startActivity(
            Intent(this, DisplayActivity::class.java)
                .putExtra(DisplayActivity.EXTRA_DISPLAY_CONFIG, config),
        )
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }
}

