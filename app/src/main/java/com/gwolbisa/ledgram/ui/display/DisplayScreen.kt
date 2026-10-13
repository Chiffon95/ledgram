package com.gwolbisa.ledgram.ui.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.ui.components.TextGlowEffectDefaults
import com.gwolbisa.ledgram.ui.components.TextGlowRenderer
import kotlinx.coroutines.delay

private const val OVERLAY_TIMEOUT_MILLIS = 3_000L

@Composable
fun DisplayScreen(
    config: DisplayConfig,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(showOverlay) {
        if (showOverlay) {
            delay(OVERLAY_TIMEOUT_MILLIS)
            showOverlay = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(config.backgroundColor))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { showOverlay = !showOverlay },
    ) {
        DisplayRenderer(
            config = config,
            modifier = Modifier.fillMaxSize(),
        )

        AnimatedVisibility(
            visible = showOverlay,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp),
        ) {
            Button(onClick = onExit) {
                Text(
                    text = stringResource(R.string.exit_display),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun DisplayRenderer(
    config: DisplayConfig,
    modifier: Modifier = Modifier,
) {
    when (config.renderStyle) {
        RenderStyle.TEXT_GLOW -> TextGlowRenderer(
            config = config,
            marqueeSpeedDpPerSecond =
                TextGlowEffectDefaults.DISPLAY_MARQUEE_SPEED_DP_PER_SECOND,
            modifier = modifier,
        )

        RenderStyle.DOT_MATRIX -> Unit
    }
}

