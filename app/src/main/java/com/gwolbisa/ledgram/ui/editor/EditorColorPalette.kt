package com.gwolbisa.ledgram.ui.editor

import androidx.annotation.StringRes
import com.gwolbisa.ledgram.R

data class EditorColorPreset(
    val value: Long,
    @StringRes val labelRes: Int,
)

val EditorColorPalette = listOf(
    EditorColorPreset(0xFFFFFFFFL, R.string.color_white),
    EditorColorPreset(0xFF000000L, R.string.color_black),
    EditorColorPreset(0xFFFF3B30L, R.string.color_red),
    EditorColorPreset(0xFFFF9500L, R.string.color_orange),
    EditorColorPreset(0xFFFFCC00L, R.string.color_yellow),
    EditorColorPreset(0xFF34C759L, R.string.color_green),
    EditorColorPreset(0xFF32ADE6L, R.string.color_cyan),
    EditorColorPreset(0xFF007AFFL, R.string.color_blue),
    EditorColorPreset(0xFFAF52DEL, R.string.color_purple),
    EditorColorPreset(0xFFFF2D55L, R.string.color_pink),
)
