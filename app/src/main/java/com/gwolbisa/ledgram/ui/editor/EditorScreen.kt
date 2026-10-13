package com.gwolbisa.ledgram.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.components.LedPreview
import com.gwolbisa.ledgram.ui.theme.LedAmber
import com.gwolbisa.ledgram.ui.theme.LedMuted
import com.gwolbisa.ledgram.util.MAX_MESSAGE_LENGTH
import com.gwolbisa.ledgram.util.addRecentColor
import com.gwolbisa.ledgram.util.codePointLength
import com.gwolbisa.ledgram.util.formatHexColor
import com.gwolbisa.ledgram.util.isLowColorContrast
import com.gwolbisa.ledgram.util.MAX_SPEED_MULTIPLIER
import com.gwolbisa.ledgram.util.MIN_SPEED_MULTIPLIER
import com.gwolbisa.ledgram.util.normalizeSpeedMultiplier
import kotlinx.coroutines.launch
import java.text.NumberFormat
import kotlin.math.roundToInt

private val EditorContentMaxWidth = 720.dp
private val EditorPreviewHeight = 180.dp
private val RecentColorsSaver = listSaver<List<Long>, Long>(
    save = { it },
    restore = { it },
)

private enum class ColorTarget {
    TEXT,
    BACKGROUND,
}

@Composable
fun EditorScreen(
    onStartDisplay: (DisplayConfig) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var uiState by rememberSaveable(stateSaver = EditorUiState.Saver) {
        mutableStateOf(EditorUiState())
    }
    var colorTargetName by rememberSaveable { mutableStateOf(ColorTarget.TEXT.name) }
    var customColorTargetName by rememberSaveable { mutableStateOf<String?>(null) }
    var recentColors by rememberSaveable(stateSaver = RecentColorsSaver) {
        mutableStateOf(emptyList())
    }
    val colorTarget = ColorTarget.valueOf(colorTargetName)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lowContrastWarning = stringResource(R.string.low_color_contrast_warning)
    val displayConfig = uiState.toDisplayConfig()
    val previewConfig = uiState.toPreviewConfig(stringResource(R.string.message_placeholder))

    fun updateColors(nextState: EditorUiState) {
        val colorsChanged = nextState.textColor != uiState.textColor ||
            nextState.backgroundColor != uiState.backgroundColor
        uiState = nextState
        if (colorsChanged && isLowColorContrast(
                nextState.textColor,
                nextState.backgroundColor,
            )
        ) {
            snackbarHostState.currentSnackbarData?.dismiss()
            coroutineScope.launch {
                snackbarHostState.showSnackbar(lowContrastWarning)
            }
        }
    }

    fun applyColor(color: Long, target: ColorTarget) {
        val nextState = when (target) {
            ColorTarget.TEXT -> uiState.copy(textColor = color)
            ColorTarget.BACKGROUND -> uiState.copy(backgroundColor = color)
        }
        updateColors(nextState)
        recentColors = addRecentColor(recentColors, color)
    }

    customColorTargetName?.let { targetName ->
        val customTarget = ColorTarget.valueOf(targetName)
        val initialColor = when (customTarget) {
            ColorTarget.TEXT -> uiState.textColor
            ColorTarget.BACKGROUND -> uiState.backgroundColor
        }
        val targetLabel = when (customTarget) {
            ColorTarget.TEXT -> stringResource(R.string.text_color)
            ColorTarget.BACKGROUND -> stringResource(R.string.background_color)
        }
        CustomColorDialog(
            initialColor = initialColor,
            targetLabel = targetLabel,
            onDismiss = { customColorTargetName = null },
            onApply = { color ->
                applyColor(color, customTarget)
                customColorTargetName = null
            },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditorTopArea(
                config = previewConfig,
                onOpenSettings = onOpenSettings,
            )
        },
        bottomBar = {
            EditorBottomActions(
                enabled = uiState.canStartDisplay,
                onStartDisplay = { onStartDisplay(displayConfig) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .widthIn(max = EditorContentMaxWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(20.dp))
                MessageInput(
                    message = uiState.message,
                    onMessageChange = { uiState = uiState.withMessage(it) },
                )
                Spacer(Modifier.height(28.dp))
                ColorSelector(
                    textColor = uiState.textColor,
                    backgroundColor = uiState.backgroundColor,
                    recentColors = recentColors,
                    target = colorTarget,
                    onTargetSelected = { colorTargetName = it.name },
                    onColorSelected = { color -> applyColor(color, colorTarget) },
                    onSwapColors = { updateColors(uiState.swapColors()) },
                    onCustomColor = { customColorTargetName = colorTarget.name },
                )
                Spacer(Modifier.height(28.dp))
                RenderStyleSelector(
                    selected = uiState.renderStyle,
                    onSelected = { uiState = uiState.copy(renderStyle = it) },
                )
                Spacer(Modifier.height(28.dp))
                MotionSelector(
                    selected = uiState.motionEffect,
                    onSelected = { uiState = uiState.copy(motionEffect = it) },
                )
                Spacer(Modifier.height(28.dp))
                VisualEffectSelector(
                    selected = uiState.visualEffect,
                    onSelected = { uiState = uiState.copy(visualEffect = it) },
                )
                Spacer(Modifier.height(28.dp))
                SpeedSelector(
                    speedMultiplier = uiState.speedMultiplier,
                    enabled = uiState.motionEffect != MotionEffect.STATIC ||
                        uiState.visualEffect != VisualEffect.NONE,
                    onSpeedChange = {
                        uiState = uiState.copy(speedMultiplier = normalizeSpeedMultiplier(it))
                    },
                )
                Spacer(Modifier.height(28.dp))
                StyleSelector(
                    bold = uiState.bold,
                    italic = uiState.italic,
                    underline = uiState.underline,
                    onBoldChange = { uiState = uiState.copy(bold = !uiState.bold) },
                    onItalicChange = { uiState = uiState.copy(italic = !uiState.italic) },
                    onUnderlineChange = {
                        uiState = uiState.copy(underline = !uiState.underline)
                    },
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SpeedSelector(
    speedMultiplier: Float,
    enabled: Boolean,
    onSpeedChange: (Float) -> Unit,
) {
    val normalizedSpeed = normalizeSpeedMultiplier(speedMultiplier)
    val formattedSpeed = remember(normalizedSpeed) {
        NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 2
        }.format(normalizedSpeed)
    }
    val valueLabel = stringResource(R.string.speed_multiplier, formattedSpeed)
    val speedLabel = stringResource(R.string.speed_label)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionLabel(speedLabel)
            Text(
                text = valueLabel,
                color = LedMuted,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Slider(
            value = normalizedSpeed,
            onValueChange = { value ->
                onSpeedChange((value * 4f).roundToInt() / 4f)
            },
            modifier = Modifier.semantics {
                contentDescription = speedLabel
                stateDescription = valueLabel
            },
            enabled = enabled,
            valueRange = MIN_SPEED_MULTIPLIER..MAX_SPEED_MULTIPLIER,
            steps = 5,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.speed_slow),
                color = LedMuted,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = stringResource(R.string.speed_fast),
                color = LedMuted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTopArea(
    config: DisplayConfig,
    onOpenSettings: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp,
    ) {
        Column {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.editor_title),
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.editor_subtitle),
                            color = LedMuted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.settings),
                        )
                    }
                },
            )
            Column(
                modifier = Modifier
                    .widthIn(max = EditorContentMaxWidth)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 20.dp, end = 20.dp, bottom = 14.dp),
            ) {
                SectionLabel(stringResource(R.string.preview_label))
                Spacer(Modifier.height(8.dp))
                LedPreview(
                    config = config,
                    modifier = Modifier.height(EditorPreviewHeight),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSelector(
    textColor: Long,
    backgroundColor: Long,
    recentColors: List<Long>,
    target: ColorTarget,
    onTargetSelected: (ColorTarget) -> Unit,
    onColorSelected: (Long) -> Unit,
    onSwapColors: () -> Unit,
    onCustomColor: () -> Unit,
) {
    val selectedColor = when (target) {
        ColorTarget.TEXT -> textColor
        ColorTarget.BACKGROUND -> backgroundColor
    }

    Column {
        SectionLabel(stringResource(R.string.color_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterChip(
                selected = target == ColorTarget.TEXT,
                onClick = { onTargetSelected(ColorTarget.TEXT) },
                label = { Text(stringResource(R.string.text_color)) },
                leadingIcon = { CurrentColorSwatch(textColor) },
            )
            FilterChip(
                selected = target == ColorTarget.BACKGROUND,
                onClick = { onTargetSelected(ColorTarget.BACKGROUND) },
                label = { Text(stringResource(R.string.background_color)) },
                leadingIcon = { CurrentColorSwatch(backgroundColor) },
            )
            OutlinedButton(onClick = onSwapColors) {
                Text(stringResource(R.string.swap_colors))
            }
            OutlinedButton(onClick = onCustomColor) {
                Text(stringResource(R.string.custom_color))
            }
        }
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EditorColorPalette.forEach { preset ->
                ColorPresetButton(
                    color = preset.value,
                    selected = selectedColor == preset.value,
                    contentDescription = stringResource(
                        R.string.select_color,
                        stringResource(preset.labelRes),
                    ),
                    onClick = { onColorSelected(preset.value) },
                )
            }
        }
        if (recentColors.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.recent_colors),
                color = LedMuted,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                recentColors.forEach { color ->
                    ColorPresetButton(
                        color = color,
                        selected = selectedColor == color,
                        contentDescription = stringResource(
                            R.string.select_recent_color,
                            formatHexColor(color),
                        ),
                        onClick = { onColorSelected(color) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentColorSwatch(color: Long) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Color(color))
            .border(1.dp, LedMuted, CircleShape),
    )
}

@Composable
private fun ColorPresetButton(
    color: Long,
    selected: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(color))
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) LedAmber else LedMuted,
                shape = CircleShape,
            )
            .clickable(onClick = onClick)
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.Button
                this.selected = selected
            },
    )
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
private fun RenderStyleSelector(
    selected: RenderStyle,
    onSelected: (RenderStyle) -> Unit,
) {
    Column {
        SectionLabel(stringResource(R.string.render_style_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FilterChip(
                selected = selected == RenderStyle.TEXT_GLOW,
                onClick = { onSelected(RenderStyle.TEXT_GLOW) },
                label = { Text(stringResource(R.string.text_glow)) },
            )
            FilterChip(
                selected = selected == RenderStyle.DOT_MATRIX,
                onClick = { onSelected(RenderStyle.DOT_MATRIX) },
                label = { Text(stringResource(R.string.dot_matrix)) },
            )
        }
        if (selected == RenderStyle.DOT_MATRIX) {
            Text(
                text = stringResource(R.string.coming_later),
                color = LedMuted,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MotionSelector(
    selected: MotionEffect,
    onSelected: (MotionEffect) -> Unit,
) {
    val motions = listOf(
        MotionEffect.STATIC to R.string.motion_static,
        MotionEffect.RIGHT_TO_LEFT to R.string.motion_right_to_left,
        MotionEffect.LEFT_TO_RIGHT to R.string.motion_left_to_right,
    )

    Column {
        SectionLabel(stringResource(R.string.motion_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            motions.forEach { (motion, label) ->
                FilterChip(
                    selected = selected == motion,
                    onClick = { onSelected(motion) },
                    label = { Text(stringResource(label)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisualEffectSelector(
    selected: VisualEffect,
    onSelected: (VisualEffect) -> Unit,
) {
    val effects = listOf(
        VisualEffect.NONE to R.string.effect_none,
        VisualEffect.BLINK to R.string.effect_blink,
        VisualEffect.PULSE to R.string.effect_pulse,
        VisualEffect.FADE to R.string.effect_fade,
    )

    Column {
        SectionLabel(stringResource(R.string.effect_label))
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            effects.forEach { (effect, label) ->
                FilterChip(
                    selected = selected == effect,
                    onClick = { onSelected(effect) },
                    label = { Text(stringResource(label)) },
                )
            }
        }
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
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingValues(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 16.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Button(
                onClick = onStartDisplay,
                enabled = enabled,
                modifier = Modifier
                    .widthIn(max = EditorContentMaxWidth)
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

