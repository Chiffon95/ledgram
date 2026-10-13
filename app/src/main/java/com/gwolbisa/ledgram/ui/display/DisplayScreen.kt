package com.gwolbisa.ledgram.ui.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.R
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.RenderStyle
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.ui.components.LedText
import com.gwolbisa.ledgram.ui.components.ledTextStyle
import kotlinx.coroutines.delay

private const val OVERLAY_TIMEOUT_MILLIS = 3_000L

private enum class MarqueeDirection {
    RIGHT_TO_LEFT,
    LEFT_TO_RIGHT,
}

private data class VisualTransform(
    val alpha: Float = 1f,
    val scale: Float = 1f,
)

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
        RenderStyle.TEXT_GLOW -> {
            val visualTransform = rememberVisualTransform(config.visualEffect)
            when (config.motionEffect) {
                MotionEffect.STATIC -> StaticTextGlowRenderer(
                    config = config,
                    visualTransform = visualTransform,
                    modifier = modifier,
                )

                MotionEffect.RIGHT_TO_LEFT -> MarqueeTextGlowRenderer(
                    config = config,
                    direction = MarqueeDirection.RIGHT_TO_LEFT,
                    visualTransform = visualTransform,
                    modifier = modifier,
                )

                MotionEffect.LEFT_TO_RIGHT -> MarqueeTextGlowRenderer(
                    config = config,
                    direction = MarqueeDirection.LEFT_TO_RIGHT,
                    visualTransform = visualTransform,
                    modifier = modifier,
                )
            }
        }

        RenderStyle.DOT_MATRIX -> Unit
    }
}

@Composable
private fun StaticTextGlowRenderer(
    config: DisplayConfig,
    visualTransform: VisualTransform,
    modifier: Modifier = Modifier,
) {
    BaseTextGlowRenderer(
        config = config,
        modifier = modifier,
        alpha = visualTransform.alpha,
        scale = visualTransform.scale,
    )
}

@Composable
private fun MarqueeTextGlowRenderer(
    config: DisplayConfig,
    direction: MarqueeDirection,
    visualTransform: VisualTransform,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val viewportWidthPx = with(density) { maxWidth.toPx() }
        val heightBasedFontSize = with(density) { (maxHeight * 0.72f).toSp() }
        val speedPxPerSecond = with(density) {
            TextGlowEffectDefaults.MARQUEE_SPEED_DP_PER_SECOND.dp.toPx()
        }
        var textWidthPx by remember { mutableIntStateOf(0) }
        val measurementsReady = viewportWidthPx > 0f && textWidthPx > 0
        val translationX = if (measurementsReady) {
            key(
                viewportWidthPx,
                textWidthPx,
                config.message,
                config.bold,
                config.italic,
                config.underline,
                direction,
            ) {
                val textWidth = textWidthPx.toFloat()
                val travelDistance = viewportWidthPx + textWidth
                val durationMillis = calculateMarqueeDurationMillis(
                    travelDistancePx = travelDistance,
                    speedPxPerSecond = speedPxPerSecond,
                )
                val start = when (direction) {
                    MarqueeDirection.RIGHT_TO_LEFT -> viewportWidthPx
                    MarqueeDirection.LEFT_TO_RIGHT -> -textWidth
                }
                val end = when (direction) {
                    MarqueeDirection.RIGHT_TO_LEFT -> -textWidth
                    MarqueeDirection.LEFT_TO_RIGHT -> viewportWidthPx
                }
                val transition = rememberInfiniteTransition(label = "marqueeTextGlow")
                val offset by transition.animateFloat(
                    initialValue = start,
                    targetValue = end,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = durationMillis,
                            easing = LinearEasing,
                        ),
                    ),
                    label = "marqueeTranslationX",
                )
                offset
            }
        } else {
            0f
        }

        Text(
            text = config.message,
            modifier = Modifier
                .wrapContentWidth(
                    align = Alignment.Start,
                    unbounded = true,
                )
                .onSizeChanged { textWidthPx = it.width }
                .graphicsLayer {
                    this.translationX = translationX
                    alpha = if (measurementsReady) visualTransform.alpha else 0f
                    scaleX = visualTransform.scale
                    scaleY = visualTransform.scale
                },
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            style = ledTextStyle(
                color = Color(config.textColor),
                fontSize = heightBasedFontSize,
                bold = config.bold,
                italic = config.italic,
                underline = config.underline,
            ),
        )
    }
}

@Composable
private fun rememberVisualTransform(effect: VisualEffect): VisualTransform = when (effect) {
    VisualEffect.NONE -> VisualTransform()
    VisualEffect.BLINK -> {
        val transition = rememberInfiniteTransition(label = "blinkTextGlow")
        val totalDuration = TextGlowEffectDefaults.BLINK_VISIBLE_MILLIS +
            TextGlowEffectDefaults.BLINK_HIDDEN_MILLIS
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = totalDuration
                    1f at 0
                    1f at TextGlowEffectDefaults.BLINK_VISIBLE_MILLIS
                    0f at TextGlowEffectDefaults.BLINK_VISIBLE_MILLIS + 1
                    0f at totalDuration - 1
                    1f at totalDuration
                },
            ),
            label = "blinkAlpha",
        )
        VisualTransform(alpha = alpha)
    }

    VisualEffect.PULSE -> {
        val transition = rememberInfiniteTransition(label = "pulseTextGlow")
        val animationSpec = infiniteRepeatable<Float>(
            animation = tween(
                durationMillis = TextGlowEffectDefaults.PULSE_HALF_CYCLE_MILLIS,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        )
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = TextGlowEffectDefaults.PULSE_MIN_ALPHA,
            animationSpec = animationSpec,
            label = "pulseAlpha",
        )
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = TextGlowEffectDefaults.PULSE_MIN_SCALE,
            animationSpec = animationSpec,
            label = "pulseScale",
        )
        VisualTransform(alpha = alpha, scale = scale)
    }

    VisualEffect.FADE -> {
        val transition = rememberInfiniteTransition(label = "fadeTextGlow")
        val alpha by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = TextGlowEffectDefaults.FADE_HALF_CYCLE_MILLIS,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "fadeAlpha",
        )
        VisualTransform(alpha = alpha)
    }
}

@Composable
private fun BaseTextGlowRenderer(
    config: DisplayConfig,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    scale: Float = 1f,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val heightBasedFontSize = with(density) { (maxHeight * 0.72f).toSp() }

        LedText(
            text = config.message,
            color = Color(config.textColor),
            bold = config.bold,
            italic = config.italic,
            underline = config.underline,
            maximumFontSize = heightBasedFontSize,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = alpha
                    scaleX = scale
                    scaleY = scale
                },
            horizontalPadding = 40.dp,
        )
    }
}

