package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LedText(
    text: String,
    color: Color,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    maximumFontSize: TextUnit,
    minimumFontSize: TextUnit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 24.dp,
) {
    BoxWithConstraints(modifier = modifier) {
        var fontSize by remember(text, maximumFontSize) {
            mutableFloatStateOf(maximumFontSize.value)
        }

        LaunchedEffect(text, maxWidth, maxHeight, maximumFontSize) {
            fontSize = maximumFontSize.value
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                color = color,
                fontSize = fontSize.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = color.copy(alpha = 0.42f),
                        offset = Offset.Zero,
                        blurRadius = 10f,
                    ),
                ),
                onTextLayout = { result ->
                    if ((result.didOverflowWidth || result.didOverflowHeight) &&
                        fontSize > minimumFontSize.value
                    ) {
                        fontSize = (fontSize * 0.9f).coerceAtLeast(minimumFontSize.value)
                    }
                },
            )
        }
    }
}

