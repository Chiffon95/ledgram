package com.gwolbisa.ledgram.ui.settings

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gwolbisa.ledgram.LEDgramApplication
import com.gwolbisa.ledgram.data.EditorPreferencesRepository
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.util.applyOrientationPolicy
import kotlinx.coroutines.CancellationException

class SettingsActivity : ComponentActivity() {
    private val ledgramApplication: LEDgramApplication
        get() = application as LEDgramApplication
    private val editorPreferencesRepository by lazy { EditorPreferencesRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySettingsOrientationPolicy()
        enableEdgeToEdge()

        setContent {
            LEDgramTheme {
                val privacyOptionsRequired by ledgramApplication.consentManager
                    .privacyOptionsRequired
                    .collectAsStateWithLifecycle()
                SettingsScreen(
                    versionName = loadVersionName(),
                    showPrivacyOptions = privacyOptionsRequired,
                    onPrivacyOptions = {
                        ledgramApplication.consentManager.showPrivacyOptionsForm(this)
                    },
                    onPrivacyPolicy = {
                        startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                    },
                    onOpenSourceLicenses = {
                        startActivity(Intent(this, OpenSourceLicensesActivity::class.java))
                    },
                    onResetEditorSettings = ::resetEditorSettings,
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

    private suspend fun resetEditorSettings(): Boolean = try {
        editorPreferencesRepository.reset()
        setResult(
            Activity.RESULT_OK,
            Intent().putExtra(EXTRA_EDITOR_SETTINGS_RESET, true),
        )
        true
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        false
    }

    companion object {
        const val EXTRA_EDITOR_SETTINGS_RESET =
            "com.gwolbisa.ledgram.extra.EDITOR_SETTINGS_RESET"
    }
}
