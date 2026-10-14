package com.gwolbisa.ledgram.ui.components

import kotlin.math.min

internal const val DOT_MATRIX_DOT_GAP_RATIO = 0.3f
internal const val DOT_MATRIX_CHARACTER_GAP_RATIO = 1.3f
internal const val DOT_MATRIX_LINE_GAP_RATIO = 1.3f
internal const val DOT_MATRIX_BOLD_OFFSET_RATIO = 0.22f
internal const val DOT_MATRIX_ITALIC_OVERSCAN_RATIO = 1.2f
internal const val DOT_MATRIX_UNDERLINE_SPACING_RATIO = 0.8f
internal const val DOT_MATRIX_UNDERLINE_HEIGHT_RATIO = 1f

internal val DOT_MATRIX_GLYPH_WIDTH_UNITS =
    DOT_MATRIX_GLYPH_WIDTH + (DOT_MATRIX_GLYPH_WIDTH - 1) * DOT_MATRIX_DOT_GAP_RATIO
internal val DOT_MATRIX_GLYPH_HEIGHT_UNITS =
    DOT_MATRIX_GLYPH_HEIGHT + (DOT_MATRIX_GLYPH_HEIGHT - 1) * DOT_MATRIX_DOT_GAP_RATIO

internal data class DotMatrixBlockSize(
    val width: Float,
    val height: Float,
)

internal data class DotMatrixStyleGeometry(
    val boldExtraWidthUnits: Float,
    val italicExtraWidthUnits: Float,
    val underlineExtraHeightUnits: Float,
) {
    val horizontalExtraWidthUnits: Float
        get() = boldExtraWidthUnits + italicExtraWidthUnits
}

internal data class DotMatrixLineLayout(
    val glyphs: List<DotMatrixGlyph>,
    val widthUnits: Float,
    val heightUnits: Float,
    val underlined: Boolean,
)

internal data class DotMatrixBlockLayout(
    val lines: List<DotMatrixLineLayout>,
    val widthUnits: Float,
    val heightUnits: Float,
    val bold: Boolean,
    val italic: Boolean,
    val underline: Boolean,
    val styleGeometry: DotMatrixStyleGeometry,
)

internal fun calculateDotMatrixStyleGeometry(
    bold: Boolean,
    italic: Boolean,
    underline: Boolean,
): DotMatrixStyleGeometry = DotMatrixStyleGeometry(
    boldExtraWidthUnits = if (bold) DOT_MATRIX_BOLD_OFFSET_RATIO else 0f,
    italicExtraWidthUnits = if (italic) DOT_MATRIX_ITALIC_OVERSCAN_RATIO else 0f,
    underlineExtraHeightUnits = if (underline) {
        DOT_MATRIX_UNDERLINE_SPACING_RATIO + DOT_MATRIX_UNDERLINE_HEIGHT_RATIO
    } else {
        0f
    },
)

internal fun calculateDotMatrixLineWidthUnits(
    glyphCount: Int,
    styleGeometry: DotMatrixStyleGeometry,
): Float {
    if (glyphCount <= 0) return 0f
    val baseWidth = glyphCount * DOT_MATRIX_GLYPH_WIDTH_UNITS +
        (glyphCount - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO
    return baseWidth + styleGeometry.horizontalExtraWidthUnits
}

internal fun createDotMatrixBlockLayout(
    message: String,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
): DotMatrixBlockLayout {
    val styleGeometry = calculateDotMatrixStyleGeometry(bold, italic, underline)
    val lines = splitDotMatrixLines(message).map { line ->
        val glyphs = line.codePoints().toArray().map(::dotMatrixGlyphFor)
        val underlined = underline && glyphs.isNotEmpty()
        DotMatrixLineLayout(
            glyphs = glyphs,
            widthUnits = calculateDotMatrixLineWidthUnits(glyphs.size, styleGeometry),
            heightUnits = DOT_MATRIX_GLYPH_HEIGHT_UNITS +
                if (underlined) styleGeometry.underlineExtraHeightUnits else 0f,
            underlined = underlined,
        )
    }
    return DotMatrixBlockLayout(
        lines = lines,
        widthUnits = lines.maxOfOrNull(DotMatrixLineLayout::widthUnits) ?: 0f,
        heightUnits = lines.sumOf { it.heightUnits.toDouble() }.toFloat() +
            (lines.size - 1).coerceAtLeast(0) * DOT_MATRIX_LINE_GAP_RATIO,
        bold = bold,
        italic = italic,
        underline = underline,
        styleGeometry = styleGeometry,
    )
}

internal fun calculateDotMatrixBlockSize(
    message: String,
    dotSize: Float,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
): DotMatrixBlockSize {
    val layout = createDotMatrixBlockLayout(message, bold, italic, underline)
    return DotMatrixBlockSize(
        width = layout.widthUnits * dotSize,
        height = layout.heightUnits * dotSize,
    )
}

internal fun calculateFittingDotSize(
    layout: DotMatrixBlockLayout,
    availableWidth: Float,
    availableHeight: Float,
    fitWidth: Boolean,
): Float {
    if (layout.heightUnits <= 0f || availableHeight <= 0f) return 0f
    val heightFit = availableHeight / layout.heightUnits
    if (!fitWidth || layout.widthUnits <= 0f) return heightFit.coerceAtLeast(0f)
    return min(heightFit, availableWidth / layout.widthUnits).coerceAtLeast(0f)
}
