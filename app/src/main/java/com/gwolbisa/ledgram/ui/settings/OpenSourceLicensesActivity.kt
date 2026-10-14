package com.gwolbisa.ledgram.ui.settings

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
)

private val licenseEntries = listOf(
    LicenseEntry(
        library = "AndroidX",
        license = "Apache License 2.0",
    ),
    LicenseEntry(
        library = "Jetpack Compose",
        license = "Apache License 2.0",
    ),
    LicenseEntry(
        library = "Material 3",
        license = "Apache License 2.0",
    ),
    LicenseEntry(
        library = "Preferences DataStore",
        license = "Apache License 2.0",
    ),
    LicenseEntry(
        library = "Kotlin",
        license = "Apache License 2.0",
    ),
    LicenseEntry(
        library = "Kotlin Coroutines",
        license = "Apache License 2.0",
    ),
)

private val thirdPartySdkNames = listOf(
    "Google Mobile Ads SDK",
    "Google User Messaging Platform SDK",
)

class OpenSourceLicensesActivity : AppCompatActivity() {
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
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.third_party_sdks_title),
            color = LedAmber,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = stringResource(R.string.third_party_sdks_body),
            color = LedMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
        )
        thirdPartySdkNames.forEach { name ->
            ThirdPartySdkRow(name)
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
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun ThirdPartySdkRow(name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
    ) {
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}
