package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
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

private val DotMatrixVerticalSafetyPadding = 16.dp

@Composable
internal fun DotMatrixRenderer(
    config: DisplayConfig,
    marqueeSpeedDpPerSecond: Float,
    modifier: Modifier = Modifier,
    staticHorizontalPadding: Dp = 40.dp,
) {
    if (config.message.isEmpty()) return

    val layout = remember(config.message, config.bold, config.italic, config.underline) {
        createDotMatrixBlockLayout(
            message = config.message,
            bold = config.bold,
            italic = config.italic,
            underline = config.underline,
        )
    }
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
            baseSpeedDpPerSecond = marqueeSpeedDpPerSecond,
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
    baseSpeedDpPerSecond: Float,
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
        val baseSpeedPxPerSecond = with(density) { baseSpeedDpPerSecond.dp.toPx() }
        val measurementsReady = viewportWidthPx > 0f && blockWidthPx > 0f
        val translationX = rememberMarqueeTranslation(
            motionEffect = config.motionEffect,
            contentIdentity = config.message,
            viewportWidthPx = viewportWidthPx,
            blockWidthPx = blockWidthPx,
            baseSpeedPxPerSecond = baseSpeedPxPerSecond,
            motionSpeedMultiplier = config.motionSpeedMultiplier,
        )

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
        val glyphWidthPx = DOT_MATRIX_GLYPH_WIDTH_UNITS * dotSizePx
        val glyphHeightPx = DOT_MATRIX_GLYPH_HEIGHT_UNITS * dotSizePx
        val characterGapPx = dotSizePx * DOT_MATRIX_CHARACTER_GAP_RATIO
        val lineGapPx = dotSizePx * DOT_MATRIX_LINE_GAP_RATIO
        val boldOffsetPx = layout.styleGeometry.boldExtraWidthUnits * dotSizePx
        val italicOverscanPx = layout.styleGeometry.italicExtraWidthUnits * dotSizePx
        val underlineSpacingPx = DOT_MATRIX_UNDERLINE_SPACING_RATIO * dotSizePx
        val radius = dotSizePx / 2f

        fun drawStyledDot(center: Offset) {
            drawCircle(color = color, radius = radius, center = center)
            if (layout.bold) {
                drawCircle(
                    color = color,
                    radius = radius,
                    center = center.copy(x = center.x + boldOffsetPx),
                )
            }
        }

        var lineStartY = 0f
        layout.lines.forEachIndexed { lineIndex, line ->
            val lineWidthPx = line.widthUnits * dotSizePx
            val lineStartX = (widthPx - lineWidthPx) / 2f
            line.glyphs.forEachIndexed { glyphIndex, glyph ->
                val glyphStartX = lineStartX + glyphIndex * (glyphWidthPx + characterGapPx)
                for (row in 0 until DOT_MATRIX_GLYPH_HEIGHT) {
                    val italicOffsetPx = if (layout.italic) {
                        italicOverscanPx *
                            (DOT_MATRIX_GLYPH_HEIGHT - 1 - row) /
                            (DOT_MATRIX_GLYPH_HEIGHT - 1)
                    } else {
                        0f
                    }
                    for (column in 0 until DOT_MATRIX_GLYPH_WIDTH) {
                        if (glyph.isActive(row, column)) {
                            drawStyledDot(
                                Offset(
                                    x = glyphStartX + column * pitchPx + radius +
                                        italicOffsetPx,
                                    y = lineStartY + row * pitchPx + radius,
                                ),
                            )
                        }
                    }
                }
            }

            if (line.underlined) {
                val underlineY = lineStartY + glyphHeightPx + underlineSpacingPx + radius
                val finalCenterX = lineStartX + lineWidthPx - radius - boldOffsetPx
                var underlineX = lineStartX + radius
                while (underlineX <= finalCenterX + 0.001f) {
                    drawStyledDot(Offset(underlineX, underlineY))
                    underlineX += pitchPx
                }
            }

            lineStartY += line.heightUnits * dotSizePx
            if (lineIndex < layout.lines.lastIndex) lineStartY += lineGapPx
        }
    }
}
