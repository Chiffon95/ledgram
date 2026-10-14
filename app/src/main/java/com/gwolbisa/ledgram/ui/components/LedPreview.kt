package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.theme.LedSurfaceRaised

@Composable
fun LedPreview(
    config: DisplayConfig,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(config.backgroundColor))
            .border(1.dp, LedSurfaceRaised, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (config.message.isEmpty()) return@Box

        LedRenderer(
            config = config,
            marqueeSpeedDpPerSecond =
                TextGlowEffectDefaults.PREVIEW_MARQUEE_SPEED_DP_PER_SECOND,
            modifier = Modifier.fillMaxSize(),
            staticHorizontalPadding = 16.dp,
        )
    }
}

