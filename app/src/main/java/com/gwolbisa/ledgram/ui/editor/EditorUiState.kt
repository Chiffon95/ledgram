package com.gwolbisa.ledgram.ui.editor

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.DisplayEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.ui.theme.BrandColorValue
import com.gwolbisa.ledgram.util.normalizeMessageForDisplay
import com.gwolbisa.ledgram.util.normalizeMessageInput

data class EditorUiState(
    val message: String = "",
    val textColor: Long = BrandColorValue.TEXT,
    val backgroundColor: Long = BrandColorValue.BACKGROUND,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val renderStyle: RenderStyle = RenderStyle.TEXT_GLOW,
    val effect: DisplayEffect = DisplayEffect.STATIC,
) {
    val canStartDisplay: Boolean
        get() = normalizeMessageForDisplay(message).isNotEmpty()

    val runtimeEffect: DisplayEffect
        get() = effect.forCurrentRuntime()

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
        effect = runtimeEffect,
    )

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
                    state.effect.name,
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
                    effect = DisplayEffect.valueOf(values[7] as String).forCurrentRuntime(),
                )
            },
        )
    }
}

private fun DisplayEffect.forCurrentRuntime(): DisplayEffect = when (this) {
    DisplayEffect.STATIC -> this
    else -> DisplayEffect.STATIC
}
