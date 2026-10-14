package com.gwolbisa.ledgram.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.RenderStyle

@Composable
internal fun LedRenderer(
    config: DisplayConfig,
    marqueeSpeedDpPerSecond: Float,
    modifier: Modifier = Modifier,
    staticHorizontalPadding: Dp = 40.dp,
) {
    // Preview and Display share this rendering contract; callers may vary only viewport,
    // approved padding, and marquee speed.
    when (config.renderStyle) {
        RenderStyle.TEXT_GLOW -> TextGlowRenderer(
            config = config,
            marqueeSpeedDpPerSecond = marqueeSpeedDpPerSecond,
            modifier = modifier,
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
