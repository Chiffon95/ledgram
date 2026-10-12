package com.gwolbisa.ledgram.ui.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.DisplayEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.ui.components.LedPreview
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.ui.theme.LedAmber
import com.gwolbisa.ledgram.ui.theme.LedMuted
import com.gwolbisa.ledgram.util.MAX_MESSAGE_LENGTH
import com.gwolbisa.ledgram.util.codePointLength
import com.gwolbisa.ledgram.util.normalizeMessageInput

@Composable
fun EditorScreen(
    onStartDisplay: (DisplayConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    var message by rememberSaveable { mutableStateOf("") }
    var bold by rememberSaveable { mutableStateOf(false) }
    var italic by rememberSaveable { mutableStateOf(false) }
    var underline by rememberSaveable { mutableStateOf(false) }
    val trimmedMessage = message.trim()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            EditorBottomActions(
                enabled = trimmedMessage.isNotEmpty(),
                onStartDisplay = {
                    onStartDisplay(
                        DisplayConfig(
                            message = message,
                            textColor = BrandColorValue.TEXT,
                            backgroundColor = BrandColorValue.BACKGROUND,
                            bold = bold,
                            italic = italic,
                            underline = underline,
                            renderStyle = RenderStyle.TEXT_GLOW,
                            effect = DisplayEffect.STATIC,
                        ),
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.editor_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.editor_subtitle),
                color = LedMuted,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(22.dp))
            SectionLabel(stringResource(R.string.preview_label))
            Spacer(Modifier.height(8.dp))
            LedPreview(
                message = message.ifEmpty { stringResource(R.string.preview_sample) },
                bold = bold,
                italic = italic,
                underline = underline,
            )
            Spacer(Modifier.height(26.dp))
            MessageInput(
                message = message,
                onMessageChange = { message = normalizeMessageInput(it) },
            )
            Spacer(Modifier.height(28.dp))
            RenderStyleSelector()
            Spacer(Modifier.height(28.dp))
            EffectSelector()
            Spacer(Modifier.height(28.dp))
            StyleSelector(
                bold = bold,
                italic = italic,
                underline = underline,
                onBoldChange = { bold = !bold },
                onItalicChange = { italic = !italic },
                onUnderlineChange = { underline = !underline },
            )
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MessageInput(message: String, onMessageChange: (String) -> Unit) {
    Column {
        SectionLabel(stringResource(R.string.message_label))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.message_placeholder)) },
            singleLine = true,
            supportingText = {
                Text(
                    text = stringResource(
                        R.string.character_counter,
                        message.codePointLength(),
                        MAX_MESSAGE_LENGTH,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RenderStyleSelector() {
    Column {
        SectionLabel(stringResource(R.string.render_style_label))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text(stringResource(R.string.text_glow)) },
            )
            FilterChip(
                selected = false,
                onClick = {},
                enabled = false,
                label = { Text(stringResource(R.string.dot_matrix)) },
            )
        }
        Text(
            text = stringResource(R.string.coming_later),
            color = LedMuted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EffectSelector() {
    val futureEffects = listOf(
        R.string.effect_right_to_left,
        R.string.effect_left_to_right,
        R.string.effect_blink,
        R.string.effect_pulse,
        R.string.effect_fade,
    )

    Column {
        SectionLabel(stringResource(R.string.effect_label))
        Spacer(Modifier.height(8.dp))
        FilterChip(
            selected = true,
            onClick = {},
            label = { Text(stringResource(R.string.effect_static)) },
        )
        futureEffects.chunked(2).forEach { rowEffects ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowEffects.forEach { label ->
                    FilterChip(
                        selected = false,
                        onClick = {},
                        enabled = false,
                        label = { Text(stringResource(label)) },
                    )
                }
            }
        }
        Text(
            text = stringResource(R.string.coming_later),
            color = LedMuted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleSelector(
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    onBoldChange: () -> Unit,
    onItalicChange: () -> Unit,
    onUnderlineChange: () -> Unit,
) {
    Column {
        SectionLabel(stringResource(R.string.text_style_label))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = bold,
                onClick = onBoldChange,
                label = { Text(stringResource(R.string.bold), fontWeight = FontWeight.Bold) },
            )
            FilterChip(
                selected = italic,
                onClick = onItalicChange,
                label = { Text(stringResource(R.string.italic)) },
            )
            FilterChip(
                selected = underline,
                onClick = onUnderlineChange,
                label = { Text(stringResource(R.string.underline)) },
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = LedAmber,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
    )
}

@Composable
private fun EditorBottomActions(enabled: Boolean, onStartDisplay: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(
                PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 16.dp),
            ),
        ) {
            Button(
                onClick = onStartDisplay,
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text(
                    text = stringResource(R.string.start_display),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

