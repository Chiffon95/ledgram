package com.gwolbisa.ledgram.ui.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.ui.components.LedRenderer
import com.gwolbisa.ledgram.ui.components.TextGlowEffectDefaults
import kotlinx.coroutines.delay

private const val OVERLAY_TIMEOUT_MILLIS = 3_000L

@Composable
fun DisplayScreen(
    config: DisplayConfig,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var controlState by remember { mutableStateOf(DisplayControlState()) }

    BackHandler(enabled = controlState.isLocked) {
        controlState = controlState.revealOverlay()
    }

    LaunchedEffect(controlState.isOverlayVisible, controlState.overlayRevision) {
        if (controlState.isOverlayVisible) {
            delay(OVERLAY_TIMEOUT_MILLIS)
            controlState = controlState.hideOverlay()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(config.backgroundColor))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { controlState = controlState.toggleOverlay() },
    ) {
        DisplayRenderer(
            config = config,
            modifier = Modifier.fillMaxSize(),
        )

        // Display controls are overlay-only and must never affect renderer bounds.
        AnimatedVisibility(
            visible = controlState.isOverlayVisible,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut() + scaleOut(targetScale = 0.85f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.End,
                    ),
                )
                .padding(14.dp),
        ) {
            Row(
                modifier = Modifier.padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (controlState.isLocked) {
                    DisplayControlButton(
                        iconResource = R.drawable.ic_lock_open,
                        contentDescription = stringResource(R.string.unlock_display),
                        onClick = { controlState = controlState.unlock() },
                    )
                } else {
                    DisplayControlButton(
                        iconResource = R.drawable.ic_lock,
                        contentDescription = stringResource(R.string.lock_display),
                        onClick = { controlState = controlState.lock() },
                    )
                    DisplayControlButton(
                        iconResource = R.drawable.ic_close,
                        contentDescription = stringResource(R.string.exit_display),
                        onClick = {
                            if (controlState.canExit) onExit()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayControlButton(
    iconResource: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.72f),
        shadowElevation = 4.dp,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(iconResource),
                contentDescription = contentDescription,
                tint = Color.White,
            )
        }
    }
}

@Composable
fun DisplayRenderer(
    config: DisplayConfig,
    modifier: Modifier = Modifier,
) {
    LedRenderer(
        config = config,
        marqueeSpeedDpPerSecond =
            TextGlowEffectDefaults.DISPLAY_MARQUEE_SPEED_DP_PER_SECOND,
        modifier = modifier,
    )
}

