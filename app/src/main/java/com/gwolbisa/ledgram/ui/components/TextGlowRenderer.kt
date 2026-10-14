package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
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
}

private val MarqueeVerticalSafetyPadding = 16.dp

internal data class MarqueeTravelBounds(
    val startPx: Float,
    val endPx: Float,
)

private data class MarqueeTextMeasurement(
    val fontSizeSp: Float,
    val widthPx: Int,
)

internal fun calculateMarqueeTravelBounds(
    viewportWidthPx: Float,
    unscaledTextWidthPx: Float,
    motionEffect: MotionEffect,
): MarqueeTravelBounds {
    val safeViewportWidth = viewportWidthPx.takeIf { it.isFinite() && it > 0f } ?: 0f
    val safeTextWidth = unscaledTextWidthPx.takeIf { it.isFinite() && it > 0f } ?: 0f
    val offscreenTextStart = if (safeTextWidth > 0f) -safeTextWidth else 0f
    return if (motionEffect == MotionEffect.RIGHT_TO_LEFT) {
        MarqueeTravelBounds(
            startPx = safeViewportWidth,
            endPx = offscreenTextStart,
        )
    } else {
        MarqueeTravelBounds(
            startPx = offscreenTextStart,
            endPx = safeViewportWidth,
        )
    }
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
    staticHorizontalPadding: Dp = 40.dp,
) {
    if (config.message.isEmpty()) return

    val visualTransform = rememberVisualTransform(
        effect = config.visualEffect,
        effectSpeedMultiplier = config.effectSpeedMultiplier,
    )
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val density = LocalDensity.current
        // Preview and Display derive the same fitting ceiling from their own viewport.
        val maximumFontSize = with(density) { (maxHeight * 0.72f).toSp() }

        val isMarquee = config.motionEffect != MotionEffect.STATIC
        // Keep marquee fitting and progress alive while Static temporarily hides it.
        Box(modifier = Modifier.fillMaxSize()) {
            LedText(
                text = config.message,
                color = Color(config.textColor),
                bold = config.bold,
                italic = config.italic,
                underline = config.underline,
                maximumFontSize = maximumFontSize,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (isMarquee) 0f else visualTransform.alpha
                        scaleX = visualTransform.scale
                        scaleY = visualTransform.scale
                        transformOrigin = visualEffectTransformOrigin(MotionEffect.STATIC)
                    },
                horizontalPadding = staticHorizontalPadding,
            )
            MarqueeTextGlow(
                config = config,
                fontSize = maximumFontSize,
                baseSpeedDpPerSecond = marqueeSpeedDpPerSecond,
                visualTransform = visualTransform,
                visible = isMarquee,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun MarqueeTextGlow(
    config: DisplayConfig,
    fontSize: TextUnit,
    baseSpeedDpPerSecond: Float,
    visualTransform: VisualTransform,
    visible: Boolean,
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
        val baseSpeedPxPerSecond = with(density) { baseSpeedDpPerSecond.dp.toPx() }
        val horizontalOverscanPx = with(density) { TextGlyphOverscan.roundToPx() * 2 }
        val measurement = remember(
            config.message,
            fontSize,
            availableHeightPx,
            config.bold,
            config.italic,
            config.underline,
            density.density,
            density.fontScale,
        ) {
            val expectedLineCount = splitExplicitTextLines(config.message).size
            fun measure(candidateSize: Float) = textMeasurer.measure(
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
            val fittedFontSize = findLargestFittingFontSize(fontSize.value) { candidateSize ->
                val result = measure(candidateSize)
                isCompleteMarqueeTextLayout(
                    textLength = config.message.length,
                    expectedLineCount = expectedLineCount,
                    lineCount = result.lineCount,
                    lastLineEnd = if (result.lineCount > 0) {
                        result.getLineEnd(result.lineCount - 1)
                    } else {
                        0
                    },
                    didOverflowHeight = result.didOverflowHeight,
                )
            }
            MarqueeTextMeasurement(
                fontSizeSp = fittedFontSize,
                widthPx = (measure(fittedFontSize).size.width + horizontalOverscanPx)
                    .coerceAtLeast(1),
            )
        }
        val measurementsReady = viewportWidthPx > 0f && measurement.widthPx > 0
        val translationX = rememberMarqueeTranslation(
            motionEffect = config.motionEffect,
            contentIdentity = config.message,
            isRunning = visible,
            viewportWidthPx = viewportWidthPx,
            blockWidthPx = measurement.widthPx.toFloat(),
            baseSpeedPxPerSecond = baseSpeedPxPerSecond,
            motionSpeedMultiplier = config.motionSpeedMultiplier,
        )

        Box(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .graphicsLayer {
                    this.translationX = translationX
                },
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    alpha = if (visible && measurementsReady) visualTransform.alpha else 0f
                    scaleX = visualTransform.scale
                    scaleY = visualTransform.scale
                    transformOrigin = visualEffectTransformOrigin(config.motionEffect)
                },
            ) {
                Text(
                    text = config.message,
                    modifier = Modifier.padding(horizontal = TextGlyphOverscan),
                    textAlign = TextAlign.Center,
                    maxLines = Int.MAX_VALUE,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    style = ledTextStyle(
                        color = Color(config.textColor),
                        fontSize = measurement.fontSizeSp.sp,
                        bold = config.bold,
                        italic = config.italic,
                        underline = config.underline,
                    ),
                )
            }
        }
    }
}
