package com.gwolbisa.ledgram.ui.editor

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
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
        get() = normalizeMessageForDisplay(message).isNotEmpty()

    fun withMessage(input: String): EditorUiState = copy(message = normalizeMessageInput(input))

    fun swapColors(): EditorUiState = copy(
        textColor = backgroundColor,
        backgroundColor = textColor,
    )

    fun toDisplayConfig(): DisplayConfig = DisplayConfig(
        message = normalizeMessageForDisplay(message),
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

    companion object {
        val Saver: Saver<EditorUiState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.message,
                    state.textColor,
                    state.backgroundColor,
                    state.bold,
                    state.italic,
                    state.underline,
                    state.renderStyle.name,
                    state.motionEffect.name,
                    state.visualEffect.name,
                    state.motionSpeedMultiplier,
                    state.effectSpeedMultiplier,
                )
            },
            restore = { values ->
                EditorUiState(
                    message = values[0] as String,
                    textColor = (values[1] as Number).toLong(),
                    backgroundColor = (values[2] as Number).toLong(),
                    bold = values[3] as Boolean,
                    italic = values[4] as Boolean,
                    underline = values[5] as Boolean,
                    renderStyle = RenderStyle.valueOf(values[6] as String),
                    motionEffect = MotionEffect.valueOf(values[7] as String),
                    visualEffect = VisualEffect.valueOf(values[8] as String),
                    motionSpeedMultiplier = if (values.size > 9) {
                        normalizeMotionSpeedMultiplier((values[9] as Number).toFloat())
                    } else {
                        DEFAULT_SPEED_MULTIPLIER
                    },
                    effectSpeedMultiplier = if (values.size > 10) {
                        normalizeEffectSpeedMultiplier((values[10] as Number).toFloat())
                    } else {
                        DEFAULT_SPEED_MULTIPLIER
                    },
                )
            },
        )
    }
}
