package com.gwolbisa.ledgram.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gwolbisa.ledgram.model.DisplayConfig
import com.gwolbisa.ledgram.model.MotionEffect
import com.gwolbisa.ledgram.util.scaledMarqueeSpeed

private val DotMatrixVerticalSafetyPadding = 16.dp

@Composable
internal fun DotMatrixRenderer(
    config: DisplayConfig,
    marqueeSpeedDpPerSecond: Float,
    modifier: Modifier = Modifier,
    staticHorizontalPadding: Dp = 40.dp,
) {
    if (config.message.isEmpty()) return

    val layout = remember(config.message) { createDotMatrixBlockLayout(config.message) }
    val visualTransform = rememberVisualTransform(
        effect = config.visualEffect,
        effectSpeedMultiplier = config.effectSpeedMultiplier,
    )

    when (config.motionEffect) {
        MotionEffect.STATIC -> StaticDotMatrixBlock(
            config = config,
            layout = layout,
            visualTransform = visualTransform,
            horizontalPadding = staticHorizontalPadding,
            modifier = modifier,
        )

        MotionEffect.RIGHT_TO_LEFT,
        MotionEffect.LEFT_TO_RIGHT,
        -> MarqueeDotMatrixBlock(
            config = config,
            layout = layout,
            visualTransform = visualTransform,
            speedDpPerSecond = scaledMarqueeSpeed(
                marqueeSpeedDpPerSecond,
                config.motionSpeedMultiplier,
            ),
            modifier = modifier,
        )
    }
}

@Composable
private fun StaticDotMatrixBlock(
    config: DisplayConfig,
    layout: DotMatrixBlockLayout,
    visualTransform: VisualTransform,
    horizontalPadding: Dp,
    modifier: Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val availableWidthPx = with(density) {
            (maxWidth - horizontalPadding * 2).toPx().coerceAtLeast(1f)
        }
        val availableHeightPx = with(density) {
            (maxHeight - DotMatrixVerticalSafetyPadding * 2).toPx().coerceAtLeast(1f)
        }
        val dotSizePx = calculateFittingDotSize(
            layout = layout,
            availableWidth = availableWidthPx,
            availableHeight = availableHeightPx,
            fitWidth = true,
        )

        Box(
            modifier = Modifier.graphicsLayer {
                alpha = visualTransform.alpha
                scaleX = visualTransform.scale
                scaleY = visualTransform.scale
                transformOrigin = visualEffectTransformOrigin(config.motionEffect)
            },
        ) {
            DotMatrixCanvas(
                layout = layout,
                dotSizePx = dotSizePx,
                color = Color(config.textColor),
            )
        }
    }
}

@Composable
private fun MarqueeDotMatrixBlock(
    config: DisplayConfig,
    layout: DotMatrixBlockLayout,
    visualTransform: VisualTransform,
    speedDpPerSecond: Float,
    modifier: Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.CenterStart,
    ) {
        val density = LocalDensity.current
        val viewportWidthPx = with(density) { maxWidth.toPx() }
        val availableHeightPx = with(density) {
            (maxHeight - DotMatrixVerticalSafetyPadding * 2).toPx().coerceAtLeast(1f)
        }
        val dotSizePx = calculateFittingDotSize(
            layout = layout,
            availableWidth = Float.POSITIVE_INFINITY,
            availableHeight = availableHeightPx,
            fitWidth = false,
        )
        val blockWidthPx = layout.widthUnits * dotSizePx
        val speedPxPerSecond = with(density) { speedDpPerSecond.dp.toPx() }
        val measurementsReady = viewportWidthPx > 0f && blockWidthPx > 0f
        val translationX = if (measurementsReady) {
            key(
                viewportWidthPx,
                blockWidthPx,
                config.message,
                config.motionEffect,
                speedPxPerSecond,
            ) {
                val bounds = calculateMarqueeTravelBounds(
                    viewportWidthPx = viewportWidthPx,
                    unscaledTextWidthPx = blockWidthPx,
                    motionEffect = config.motionEffect,
                )
                val durationMillis = calculateMarqueeDurationMillis(
                    travelDistancePx = viewportWidthPx + blockWidthPx,
                    speedPxPerSecond = speedPxPerSecond,
                )
                val transition = rememberInfiniteTransition(label = "marqueeDotMatrix")
                val offset by transition.animateFloat(
                    initialValue = bounds.startPx,
                    targetValue = bounds.endPx,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                    ),
                    label = "dotMatrixTranslationX",
                )
                offset
            }
        } else {
            0f
        }

        Box(
            modifier = Modifier
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .graphicsLayer { this.translationX = translationX },
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    alpha = if (measurementsReady) visualTransform.alpha else 0f
                    scaleX = visualTransform.scale
                    scaleY = visualTransform.scale
                    transformOrigin = visualEffectTransformOrigin(config.motionEffect)
                },
            ) {
                DotMatrixCanvas(
                    layout = layout,
                    dotSizePx = dotSizePx,
                    color = Color(config.textColor),
                )
            }
        }
    }
}

@Composable
private fun DotMatrixCanvas(
    layout: DotMatrixBlockLayout,
    dotSizePx: Float,
    color: Color,
) {
    val density = LocalDensity.current
    val widthPx = (layout.widthUnits * dotSizePx).coerceAtLeast(1f)
    val heightPx = (layout.heightUnits * dotSizePx).coerceAtLeast(1f)
    val widthDp = with(density) { widthPx.toDp() }
    val heightDp = with(density) { heightPx.toDp() }

    Canvas(modifier = Modifier.requiredSize(widthDp, heightDp)) {
        val dotGapPx = dotSizePx * DOT_MATRIX_DOT_GAP_RATIO
        val pitchPx = dotSizePx + dotGapPx
        val glyphWidthPx = DOT_MATRIX_GLYPH_WIDTH * dotSizePx +
            (DOT_MATRIX_GLYPH_WIDTH - 1) * dotGapPx
        val glyphHeightPx = DOT_MATRIX_GLYPH_HEIGHT * dotSizePx +
            (DOT_MATRIX_GLYPH_HEIGHT - 1) * dotGapPx
        val characterGapPx = dotSizePx * DOT_MATRIX_CHARACTER_GAP_RATIO
        val lineGapPx = dotSizePx * DOT_MATRIX_LINE_GAP_RATIO
        val radius = dotSizePx / 2f

        layout.lines.forEachIndexed { lineIndex, line ->
            val lineWidthPx = line.widthUnits * dotSizePx
            val lineStartX = (widthPx - lineWidthPx) / 2f
            val lineStartY = lineIndex * (glyphHeightPx + lineGapPx)
            line.glyphs.forEachIndexed { glyphIndex, glyph ->
                val glyphStartX = lineStartX + glyphIndex * (glyphWidthPx + characterGapPx)
                for (row in 0 until DOT_MATRIX_GLYPH_HEIGHT) {
                    for (column in 0 until DOT_MATRIX_GLYPH_WIDTH) {
                        if (glyph.isActive(row, column)) {
                            drawCircle(
                                color = color,
                                radius = radius,
                                center = Offset(
                                    x = glyphStartX + column * pitchPx + radius,
                                    y = lineStartY + row * pitchPx + radius,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
