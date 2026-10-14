package com.gwolbisa.ledgram.ui.settings

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.ui.theme.LEDgramTheme
import com.gwolbisa.ledgram.ui.theme.LedAmber
import com.gwolbisa.ledgram.ui.theme.LedMuted
import com.gwolbisa.ledgram.util.applyOrientationPolicy

private data class LicenseEntry(
    val library: String,
    val license: String,
    val projectUrl: String,
)

private val licenseEntries = listOf(
    LicenseEntry(
        library = "AndroidX",
        license = "The Apache Software License, Version 2.0",
        projectUrl = "https://developer.android.com/jetpack/androidx",
    ),
    LicenseEntry(
        library = "Jetpack Compose",
        license = "The Apache Software License, Version 2.0",
        projectUrl = "https://developer.android.com/compose",
    ),
    LicenseEntry(
        library = "Material 3",
        license = "The Apache Software License, Version 2.0",
        projectUrl = "https://developer.android.com/jetpack/androidx/releases/compose-material3",
    ),
    LicenseEntry(
        library = "Preferences DataStore",
        license = "The Apache Software License, Version 2.0",
        projectUrl = "https://developer.android.com/topic/libraries/architecture/datastore",
    ),
    LicenseEntry(
        library = "Kotlin",
        license = "Apache-2.0",
        projectUrl = "https://kotlinlang.org/",
    ),
    LicenseEntry(
        library = "Kotlin Coroutines",
        license = "The Apache Software License, Version 2.0",
        projectUrl = "https://github.com/Kotlin/kotlinx.coroutines",
    ),
    LicenseEntry(
        library = "Google Mobile Ads SDK",
        license = "Android Software Development Kit License",
        projectUrl = "https://developers.google.com/admob/android/quick-start",
    ),
    LicenseEntry(
        library = "Google User Messaging Platform SDK",
        license = "Android Software Development Kit License",
        projectUrl = "https://developers.google.com/admob/android/privacy",
    ),
)

class OpenSourceLicensesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDocumentOrientationPolicy()
        enableEdgeToEdge()
        setContent {
            LEDgramTheme {
                OpenSourceLicensesScreen(onBack = onBackPressedDispatcher::onBackPressed)
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyDocumentOrientationPolicy(newConfig)
    }

    private fun applyDocumentOrientationPolicy(
        configuration: Configuration = resources.configuration,
    ) {
        applyOrientationPolicy(
            compactOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            configuration = configuration,
        )
    }
}

@Composable
private fun OpenSourceLicensesScreen(onBack: () -> Unit) {
    SettingsDocumentScreen(
        title = stringResource(R.string.settings_open_source_licenses),
        onBack = onBack,
    ) {
        Text(
            text = stringResource(R.string.open_source_licenses_intro),
            color = LedMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(16.dp))
        licenseEntries.forEach { entry ->
            LicenseRow(entry)
        }
    }
}

@Composable
private fun LicenseRow(entry: LicenseEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
    ) {
        Text(
            text = entry.library,
            color = LedAmber,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = entry.license,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = entry.projectUrl,
            color = LedMuted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
