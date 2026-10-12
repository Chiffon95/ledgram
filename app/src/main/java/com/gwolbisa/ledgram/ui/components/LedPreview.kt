package com.gwolbisa.ledgram.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gwolbisa.ledgram.ui.theme.LedSurfaceRaised

@Composable
fun LedPreview(
    message: String,
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(216.dp)
            .clip(shape)
            .background(Color.Black)
            .border(1.dp, LedSurfaceRaised, shape),
    ) {
        LedText(
            text = message,
            color = Color.White,
            bold = bold,
            italic = italic,
            underline = underline,
            maximumFontSize = 52.sp,
            minimumFontSize = 16.sp,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

