package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

internal val TextGlyphOverscan = 6.dp

@Composable
fun LedText(
    text: String,
    color: Color,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    maximumFontSize: TextUnit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 24.dp,
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val verticalPadding = 16.dp
        val availableWidthPx = with(density) {
            (maxWidth - horizontalPadding * 2 - TextGlyphOverscan * 2)
                .toPx()
                .roundToInt()
                .coerceAtLeast(1)
        }
        val availableHeightPx = with(density) {
            (maxHeight - verticalPadding * 2).toPx().roundToInt().coerceAtLeast(1)
        }
        val fontSize = remember(
            text,
            maximumFontSize,
            availableWidthPx,
            availableHeightPx,
            bold,
            italic,
            underline,
            density.fontScale,
        ) {
            if (text.isEmpty()) {
                maximumFontSize.value
            } else {
                findLargestFittingFontSize(maximumFontSize.value) { candidateSize ->
                    val result = textMeasurer.measure(
                        text = AnnotatedString(text),
                        style = ledTextStyle(
                            color = color,
                            fontSize = candidateSize.sp,
                            bold = bold,
                            italic = italic,
                            underline = underline,
                        ),
                        overflow = TextOverflow.Clip,
                        softWrap = true,
                        maxLines = Int.MAX_VALUE,
                        constraints = Constraints(
                            maxWidth = availableWidthPx,
                            maxHeight = availableHeightPx,
                        ),
                    )
                    !result.didOverflowWidth && !result.didOverflowHeight
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TextGlyphOverscan),
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
                softWrap = true,
                overflow = TextOverflow.Clip,
                style = ledTextStyle(
                    color = color,
                    fontSize = fontSize.sp,
                    bold = bold,
                    italic = italic,
                    underline = underline,
                ),
            )
        }
    }
}

internal fun ledTextStyle(
    color: Color,
    fontSize: TextUnit,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
): TextStyle = TextStyle(
    color = color,
    fontSize = fontSize,
    fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
    textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    textMotion = TextMotion.Animated,
    shadow = Shadow(
        color = color.copy(alpha = 0.42f),
        offset = Offset.Zero,
        blurRadius = 10f,
    ),
)

