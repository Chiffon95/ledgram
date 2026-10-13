package com.gwolbisa.ledgram.ui.settings

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.util.applyOrientationPolicy

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySettingsOrientationPolicy()
        enableEdgeToEdge()

        setContent {
            LEDgramTheme {
                SettingsScreen(
                    versionName = loadVersionName(),
                    onBack = onBackPressedDispatcher::onBackPressed,
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applySettingsOrientationPolicy(newConfig)
    }

    private fun applySettingsOrientationPolicy(
        configuration: Configuration = resources.configuration,
    ) {
        applyOrientationPolicy(
            compactOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            configuration = configuration,
        )
    }

    private fun loadVersionName(): String {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)
        }
        return packageInfo.versionName.orEmpty()
    }
}
