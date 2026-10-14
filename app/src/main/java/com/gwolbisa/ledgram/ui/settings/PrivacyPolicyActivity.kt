package com.gwolbisa.ledgram.ui.settings

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDocumentOrientationPolicy()
        enableEdgeToEdge()
        setContent {
            LEDgramTheme {
                PrivacyPolicyScreen(onBack = onBackPressedDispatcher::onBackPressed)
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
private fun PrivacyPolicyScreen(onBack: () -> Unit) {
    SettingsDocumentScreen(
        title = stringResource(R.string.settings_privacy_policy),
        onBack = onBack,
    ) {
        Text(
            text = stringResource(
                R.string.privacy_policy_effective_date,
                stringResource(R.string.privacy_policy_date),
            ),
            color = LedMuted,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(20.dp))
        PrivacyPolicySection(
            title = stringResource(R.string.privacy_policy_local_title),
            body = stringResource(R.string.privacy_policy_local_body),
        )
        PrivacyPolicySection(
            title = stringResource(R.string.privacy_policy_content_title),
            body = stringResource(R.string.privacy_policy_content_body),
        )
        PrivacyPolicySection(
            title = stringResource(R.string.privacy_policy_ads_title),
            body = stringResource(R.string.privacy_policy_ads_body),
        )
        PrivacyPolicySection(
            title = stringResource(R.string.privacy_policy_controls_title),
            body = stringResource(R.string.privacy_policy_controls_body),
        )
        PrivacyPolicySection(
            title = stringResource(R.string.privacy_policy_changes_title),
            body = stringResource(R.string.privacy_policy_changes_body),
        )
    }
}

@Composable
private fun PrivacyPolicySection(title: String, body: String) {
    Text(
        text = title,
        color = LedAmber,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
    )
    Text(
        text = body,
        style = MaterialTheme.typography.bodyLarge,
    )
    Spacer(Modifier.height(12.dp))
}
