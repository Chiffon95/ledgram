package com.gwolbisa.ledgram.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.ui.theme.LedAmber
import com.gwolbisa.ledgram.ui.theme.LedMuted
import kotlinx.coroutines.launch

private val SettingsContentMaxWidth = 720.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    versionName: String,
    selectedLanguage: AppLanguage,
    showPrivacyOptions: Boolean,
    onPrivacyOptions: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onOpenSourceLicenses: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onResetEditorSettings: suspend () -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showResetDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pendingLanguage by remember { mutableStateOf<AppLanguage?>(null) }
    val resetSuccessMessage = stringResource(R.string.settings_reset_success)

    LaunchedEffect(pendingLanguage) {
        val language = pendingLanguage ?: return@LaunchedEffect
        // Wait until Compose has committed a frame without the dialog before changing locale.
        withFrameNanos { }
        onLanguageSelected(language)
        pendingLanguage = null
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { language ->
                showLanguageDialog = false
                if (language != selectedLanguage) pendingLanguage = language
            },
            onDismiss = { showLanguageDialog = false },
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.settings_reset_dialog_title)) },
            text = { Text(stringResource(R.string.settings_reset_dialog_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        coroutineScope.launch {
                            if (onResetEditorSettings()) {
                                snackbarHostState.showSnackbar(resetSuccessMessage)
                            }
                        }
                    },
                ) {
                    Text(stringResource(R.string.settings_reset_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = SettingsContentMaxWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                SettingsSectionTitle(stringResource(R.string.settings_section_app))
                SettingsRow(
                    label = stringResource(R.string.settings_app_name),
                    value = stringResource(R.string.app_name),
                )
                SettingsRow(
                    label = stringResource(R.string.settings_version),
                    value = versionName,
                )
                SettingsRow(
                    label = stringResource(R.string.settings_developer),
                    value = stringResource(R.string.studio_name),
                )
                SettingsActionRow(
                    label = stringResource(R.string.settings_language),
                    value = appLanguageLabel(selectedLanguage),
                    onClick = { showLanguageDialog = true },
                )

                SettingsSectionTitle(stringResource(R.string.settings_privacy_legal))
                SettingsActionRow(
                    label = stringResource(R.string.settings_privacy_policy),
                    onClick = onPrivacyPolicy,
                )
                SettingsActionRow(
                    label = stringResource(R.string.settings_open_source_licenses),
                    onClick = onOpenSourceLicenses,
                )
                if (showPrivacyOptions) {
                    SettingsActionRow(
                        label = stringResource(R.string.settings_privacy_options),
                        onClick = onPrivacyOptions,
                    )
                }

                SettingsSectionTitle(stringResource(R.string.settings_section_data))
                SettingsActionRow(
                    label = stringResource(R.string.settings_reset_editor),
                    onClick = { showResetDialog = true },
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SettingsActionRow(
    label: String,
    value: String? = null,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            value?.let {
                Text(
                    text = it,
                    color = LedMuted,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun LanguageSelectionDialog(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                role = Role.RadioButton,
                                onClick = { onLanguageSelected(language) },
                            )
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = language == selectedLanguage,
                            onClick = null,
                        )
                        Text(
                            text = appLanguageLabel(language),
                            modifier = Modifier.padding(start = 12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun appLanguageLabel(language: AppLanguage): String = when (language) {
    AppLanguage.SYSTEM_DEFAULT -> stringResource(R.string.settings_system_default)
    AppLanguage.KOREAN -> stringResource(R.string.settings_language_korean)
    AppLanguage.ENGLISH -> stringResource(R.string.settings_language_english)
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        color = LedAmber,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
    )
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = value,
                color = LedMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
