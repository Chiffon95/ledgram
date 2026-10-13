package com.gwolbisa.ledgram.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
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

private val MarqueeVerticalSafetyPadding = 16.dp

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

internal data class MarqueeTravelBounds(
    val startPx: Float,
    val endPx: Float,
)

internal fun calculateMarqueeTravelBounds(
    viewportWidthPx: Float,
    unscaledTextWidthPx: Float,
    motionEffect: MotionEffect,
): MarqueeTravelBounds = if (motionEffect == MotionEffect.RIGHT_TO_LEFT) {
    MarqueeTravelBounds(
        startPx = viewportWidthPx,
        endPx = -unscaledTextWidthPx,
    )
} else {
    MarqueeTravelBounds(
        startPx = -unscaledTextWidthPx,
        endPx = viewportWidthPx,
    )
}

internal fun visualEffectTransformOrigin(motionEffect: MotionEffect): TransformOrigin =
    when (motionEffect) {
        MotionEffect.STATIC -> TransformOrigin.Center
        MotionEffect.RIGHT_TO_LEFT -> TransformOrigin(
            pivotFractionX = 0f,
            pivotFractionY = 0.5f,
        )

        MotionEffect.LEFT_TO_RIGHT -> TransformOrigin(
            pivotFractionX = 1f,
            pivotFractionY = 0.5f,
        )
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
                        transformOrigin = visualEffectTransformOrigin(config.motionEffect)
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
        val textMeasurer = rememberTextMeasurer()
        val viewportWidthPx = with(density) { maxWidth.toPx() }
        val availableHeightPx = with(density) {
            (maxHeight - MarqueeVerticalSafetyPadding * 2)
                .toPx()
                .roundToInt()
                .coerceAtLeast(1)
        }
        val speedPxPerSecond = with(density) { speedDpPerSecond.dp.toPx() }
        val fittedFontSize = remember(
            config.message,
            fontSize,
            availableHeightPx,
            config.bold,
            config.italic,
            config.underline,
            density.fontScale,
        ) {
            findLargestFittingFontSize(fontSize.value) { candidateSize ->
                val result = textMeasurer.measure(
                    text = AnnotatedString(config.message),
                    style = ledTextStyle(
                        color = Color(config.textColor),
                        fontSize = candidateSize.sp,
                        bold = config.bold,
                        italic = config.italic,
                        underline = config.underline,
                    ),
                    overflow = TextOverflow.Clip,
                    softWrap = false,
                    maxLines = Int.MAX_VALUE,
                    constraints = Constraints(
                        maxWidth = Constraints.Infinity,
                        maxHeight = availableHeightPx,
                    ),
                )
                isCompleteMarqueeTextLayout(
                    textLength = config.message.length,
                    lineCount = result.lineCount,
                    lastLineEnd = if (result.lineCount > 0) {
                        result.getLineEnd(result.lineCount - 1)
                    } else {
                        0
                    },
                    didOverflowHeight = result.didOverflowHeight,
                )
            }
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
                config.motionEffect,
                speedPxPerSecond,
            ) {
                val textWidth = textWidthPx.toFloat()
                val durationMillis = calculateMarqueeDurationMillis(
                    travelDistancePx = viewportWidthPx + textWidth,
                    speedPxPerSecond = speedPxPerSecond,
                )
                val travelBounds = calculateMarqueeTravelBounds(
                    viewportWidthPx = viewportWidthPx,
                    unscaledTextWidthPx = textWidth,
                    motionEffect = config.motionEffect,
                )
                val transition = rememberInfiniteTransition(label = "marqueeTextGlow")
                val offset by transition.animateFloat(
                    initialValue = travelBounds.startPx,
                    targetValue = travelBounds.endPx,
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

        Box(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .graphicsLayer {
                    this.translationX = translationX
                },
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    alpha = if (measurementsReady) visualTransform.alpha else 0f
                    scaleX = visualTransform.scale
                    scaleY = visualTransform.scale
                    transformOrigin = visualEffectTransformOrigin(config.motionEffect)
                },
            ) {
                Text(
                    text = config.message,
                    modifier = Modifier
                        .onSizeChanged { textWidthPx = it.width }
                        .padding(horizontal = TextGlyphOverscan),
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    style = ledTextStyle(
                        color = Color(config.textColor),
                        fontSize = fittedFontSize.sp,
                        bold = config.bold,
                        italic = config.italic,
                        underline = config.underline,
                    ),
                )
            }
        }
    }
}
