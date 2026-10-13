package com.gwolbisa.ledgram.data

import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.editor.EditorUiState
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.util.DEFAULT_SPEED_MULTIPLIER
import com.gwolbisa.ledgram.util.normalizeEffectSpeedMultiplier
import com.gwolbisa.ledgram.util.normalizeMessageInput
import com.gwolbisa.ledgram.util.normalizeMotionSpeedMultiplier

data class EditorPreferences(
    val message: String = "",
    val textColor: Long = BrandColorValue.TEXT,
    val backgroundColor: Long = BrandColorValue.BACKGROUND,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val renderStyle: RenderStyle = RenderStyle.TEXT_GLOW,
    val motionEffect: MotionEffect = MotionEffect.STATIC,
    val visualEffect: VisualEffect = VisualEffect.NONE,
    val motionSpeedMultiplier: Float = DEFAULT_SPEED_MULTIPLIER,
    val effectSpeedMultiplier: Float = DEFAULT_SPEED_MULTIPLIER,
)

internal fun EditorPreferences.toEditorUiState(): EditorUiState = EditorUiState(
    message = normalizeMessageInput(message),
    textColor = normalizeStoredColor(textColor, BrandColorValue.TEXT),
    backgroundColor = normalizeStoredColor(backgroundColor, BrandColorValue.BACKGROUND),
    bold = bold,
    italic = italic,
    underline = underline,
    renderStyle = renderStyle,
    motionEffect = motionEffect,
    visualEffect = visualEffect,
    motionSpeedMultiplier = normalizeMotionSpeedMultiplier(motionSpeedMultiplier),
    effectSpeedMultiplier = normalizeEffectSpeedMultiplier(effectSpeedMultiplier),
)

internal fun EditorUiState.toEditorPreferences(): EditorPreferences = EditorPreferences(
    message = normalizeMessageInput(message),
    textColor = normalizeStoredColor(textColor, BrandColorValue.TEXT),
    backgroundColor = normalizeStoredColor(backgroundColor, BrandColorValue.BACKGROUND),
    bold = bold,
    italic = italic,
    underline = underline,
    renderStyle = renderStyle,
    motionEffect = motionEffect,
    visualEffect = visualEffect,
    motionSpeedMultiplier = normalizeMotionSpeedMultiplier(motionSpeedMultiplier),
    effectSpeedMultiplier = normalizeEffectSpeedMultiplier(effectSpeedMultiplier),
)

internal fun normalizeStoredColor(color: Long, fallback: Long): Long =
    if (color in 0L..0xFFFFFFFFL) color else fallback
