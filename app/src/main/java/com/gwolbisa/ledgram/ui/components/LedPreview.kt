package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.RenderStyle
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

        when (config.renderStyle) {
            RenderStyle.TEXT_GLOW -> TextGlowRenderer(
                config = config,
                marqueeSpeedDpPerSecond =
                    TextGlowEffectDefaults.PREVIEW_MARQUEE_SPEED_DP_PER_SECOND,
                modifier = Modifier.fillMaxSize(),
                maximumFontSize = 52.sp,
                staticHorizontalPadding = 16.dp,
            )

            RenderStyle.DOT_MATRIX -> Text(
                text = "${stringResource(R.string.dot_matrix)} · ${stringResource(R.string.coming_later)}",
                color = Color(config.textColor),
                fontWeight = if (config.bold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (config.italic) FontStyle.Italic else FontStyle.Normal,
                textDecoration = if (config.underline) {
                    TextDecoration.Underline
                } else {
                    TextDecoration.None
                },
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

