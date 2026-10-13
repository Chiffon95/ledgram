package com.gwolbisa.ledgram.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.model.VisualEffect
import com.gwolbisa.ledgram.util.scaledEffectDurationMillis
import com.gwolbisa.ledgram.util.scaledMarqueeSpeed
import kotlin.math.roundToInt

internal object TextGlowEffectDefaults {
    const val BLINK_VISIBLE_MILLIS = 650
    const val BLINK_HIDDEN_MILLIS = 350
    const val PULSE_HALF_CYCLE_MILLIS = 700
    const val FADE_HALF_CYCLE_MILLIS = 900
    const val PULSE_MIN_ALPHA = 0.55f
    // Pulse starts at 1f and only scales down, so fitted text never grows past its bounds.
    const val PULSE_MIN_SCALE = 0.94f
    const val DISPLAY_MARQUEE_SPEED_DP_PER_SECOND = 90f
    const val PREVIEW_MARQUEE_SPEED_DP_PER_SECOND = 55f
    const val MIN_MARQUEE_DURATION_MILLIS = 1_000
}

internal fun calculateMarqueeDurationMillis(
    travelDistancePx: Float,
    speedPxPerSecond: Float,
): Int {
    if (!travelDistancePx.isFinite() || travelDistancePx <= 0f ||
        !speedPxPerSecond.isFinite() || speedPxPerSecond <= 0f
    ) {
        return TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS
    }

    val duration = travelDistancePx / speedPxPerSecond * 1_000f
    return duration
        .coerceAtMost(Int.MAX_VALUE.toFloat())
        .roundToInt()
        .coerceAtLeast(TextGlowEffectDefaults.MIN_MARQUEE_DURATION_MILLIS)
}

@Composable
internal fun TextGlowRenderer(
    config: DisplayConfig,
    marqueeSpeedDpPerSecond: Float,
    modifier: Modifier = Modifier,
    maximumFontSize: TextUnit = TextUnit.Unspecified,
    staticHorizontalPadding: Dp = 40.dp,
) {
    if (config.message.isEmpty()) return

    val visualTransform = rememberVisualTransform(
        effect = config.visualEffect,
        effectSpeedMultiplier = config.effectSpeedMultiplier,
    )
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val density = LocalDensity.current
        val heightBasedFontSize = with(density) { (maxHeight * 0.72f).toSp() }
        val effectiveMaximumFontSize = if (maximumFontSize.isSpecified) {
            minOf(heightBasedFontSize.value, maximumFontSize.value).sp
        } else {
            heightBasedFontSize
        }

        when (config.motionEffect) {
            MotionEffect.STATIC -> LedText(
                text = config.message,
                color = Color(config.textColor),
                bold = config.bold,
                italic = config.italic,
                underline = config.underline,
                maximumFontSize = effectiveMaximumFontSize,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = visualTransform.alpha
                        scaleX = visualTransform.scale
                        scaleY = visualTransform.scale
                    },
                horizontalPadding = staticHorizontalPadding,
            )

            MotionEffect.RIGHT_TO_LEFT,
            MotionEffect.LEFT_TO_RIGHT,
            -> MarqueeTextGlow(
                config = config,
                fontSize = effectiveMaximumFontSize,
                speedDpPerSecond = scaledMarqueeSpeed(
                    marqueeSpeedDpPerSecond,
                    config.motionSpeedMultiplier,
                ),
                visualTransform = visualTransform,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

private data class VisualTransform(
    val alpha: Float = 1f,
    val scale: Float = 1f,
)

@Composable
private fun MarqueeTextGlow(
    config: DisplayConfig,
    fontSize: TextUnit,
    speedDpPerSecond: Float,
    visualTransform: VisualTransform,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val viewportWidthPx = with(density) { maxWidth.toPx() }
        val speedPxPerSecond = with(density) { speedDpPerSecond.dp.toPx() }
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
                config.motionEffect,
                speedPxPerSecond,
            ) {
                val textWidth = textWidthPx.toFloat()
                val durationMillis = calculateMarqueeDurationMillis(
                    travelDistancePx = viewportWidthPx + textWidth,
                    speedPxPerSecond = speedPxPerSecond,
                )
                val start = if (config.motionEffect == MotionEffect.RIGHT_TO_LEFT) {
                    viewportWidthPx
                } else {
                    -textWidth
                }
                val end = if (config.motionEffect == MotionEffect.RIGHT_TO_LEFT) {
                    -textWidth
                } else {
                    viewportWidthPx
                }
                val transition = rememberInfiniteTransition(label = "marqueeTextGlow")
                val offset by transition.animateFloat(
                    initialValue = start,
                    targetValue = end,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = durationMillis, easing = LinearEasing),
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
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .onSizeChanged { textWidthPx = it.width }
                .graphicsLayer {
                    this.translationX = translationX
                    alpha = if (measurementsReady) visualTransform.alpha else 0f
                    scaleX = visualTransform.scale
                    scaleY = visualTransform.scale
                }
                .padding(horizontal = TextGlyphOverscan),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            style = ledTextStyle(
                color = Color(config.textColor),
                fontSize = fontSize,
                bold = config.bold,
                italic = config.italic,
                underline = config.underline,
            ),
        )
    }
}

@Composable
private fun rememberVisualTransform(
    effect: VisualEffect,
    effectSpeedMultiplier: Float,
): VisualTransform = when (effect) {
    VisualEffect.NONE -> VisualTransform()
    VisualEffect.BLINK -> {
        val transition = rememberInfiniteTransition(label = "blinkTextGlow")
        val visibleDuration = scaledEffectDurationMillis(
            TextGlowEffectDefaults.BLINK_VISIBLE_MILLIS,
            effectSpeedMultiplier,
        )
        val hiddenDuration = scaledEffectDurationMillis(
            TextGlowEffectDefaults.BLINK_HIDDEN_MILLIS,
            effectSpeedMultiplier,
        )
        val totalDuration = visibleDuration + hiddenDuration
        val alpha by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = totalDuration
                    1f at 0
                    1f at visibleDuration
                    0f at (visibleDuration + 1).coerceAtMost(totalDuration)
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
                durationMillis = scaledEffectDurationMillis(
                    TextGlowEffectDefaults.PULSE_HALF_CYCLE_MILLIS,
                    effectSpeedMultiplier,
                ),
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
                    durationMillis = scaledEffectDurationMillis(
                        TextGlowEffectDefaults.FADE_HALF_CYCLE_MILLIS,
                        effectSpeedMultiplier,
                    ),
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "fadeAlpha",
        )
        VisualTransform(alpha = alpha)
    }
}
