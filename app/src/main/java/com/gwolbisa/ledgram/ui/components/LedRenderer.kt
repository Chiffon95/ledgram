package com.gwolbisa.ledgram.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.RenderStyle

@Composable
internal fun LedRenderer(
    config: DisplayConfig,
    marqueeSpeedDpPerSecond: Float,
    modifier: Modifier = Modifier,
    maximumFontSize: TextUnit = TextUnit.Unspecified,
    staticHorizontalPadding: Dp = 40.dp,
) {
    when (config.renderStyle) {
        RenderStyle.TEXT_GLOW -> TextGlowRenderer(
            config = config,
            marqueeSpeedDpPerSecond = marqueeSpeedDpPerSecond,
            modifier = modifier,
            maximumFontSize = maximumFontSize,
            staticHorizontalPadding = staticHorizontalPadding,
        )

        RenderStyle.DOT_MATRIX -> DotMatrixRenderer(
            config = config,
            marqueeSpeedDpPerSecond = marqueeSpeedDpPerSecond,
            modifier = modifier,
            staticHorizontalPadding = staticHorizontalPadding,
        )
    }
}
