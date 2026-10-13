package com.gwolbisa.ledgram.ui.editor

import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.util.normalizeMessageForDisplay
import com.gwolbisa.ledgram.util.normalizeMessageInput
import com.gwolbisa.ledgram.util.DEFAULT_SPEED_MULTIPLIER
import com.gwolbisa.ledgram.util.normalizeEffectSpeedMultiplier
import com.gwolbisa.ledgram.util.normalizeMotionSpeedMultiplier

data class EditorUiState(
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
) {
    val canStartDisplay: Boolean
        get() = normalizeMessageForDisplay(message, motionEffect).isNotEmpty()

    fun withMessage(input: String): EditorUiState = copy(message = normalizeMessageInput(input))

    fun swapColors(): EditorUiState = copy(
        textColor = backgroundColor,
        backgroundColor = textColor,
    )

    fun toDisplayConfig(): DisplayConfig = DisplayConfig(
        message = normalizeMessageForDisplay(message, motionEffect),
        textColor = textColor,
        backgroundColor = backgroundColor,
        bold = bold,
        italic = italic,
        underline = underline,
        renderStyle = renderStyle,
        motionEffect = motionEffect,
        visualEffect = visualEffect,
        motionSpeedMultiplier = normalizeMotionSpeedMultiplier(motionSpeedMultiplier),
        effectSpeedMultiplier = normalizeEffectSpeedMultiplier(effectSpeedMultiplier),
    )

    fun toPreviewConfig(sampleMessage: String): DisplayConfig {
        val displayConfig = toDisplayConfig()
        return if (displayConfig.message.isEmpty()) {
            displayConfig.copy(message = sampleMessage)
        } else {
            displayConfig
        }
    }

}
