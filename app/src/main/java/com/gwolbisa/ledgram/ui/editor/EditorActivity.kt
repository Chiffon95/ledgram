package com.gwolbisa.ledgram.ui.editor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.display.DisplayActivity
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme

class EditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LEDgramTheme {
                EditorScreen(onStartDisplay = ::openDisplay)
            }
        }
    }

    private fun openDisplay(config: DisplayConfig) {
        startActivity(
            Intent(this, DisplayActivity::class.java)
                .putExtra(DisplayActivity.EXTRA_DISPLAY_CONFIG, config),
        )
    }
}

