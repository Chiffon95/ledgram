package com.gwolbisa.ledgram.ui.components

import kotlin.math.min

internal const val DOT_MATRIX_DOT_GAP_RATIO = 0.3f
internal const val DOT_MATRIX_CHARACTER_GAP_RATIO = 1.3f
internal const val DOT_MATRIX_LINE_GAP_RATIO = 1.3f
internal const val DOT_MATRIX_NORMAL_RADIUS_RATIO = 0.5f
internal const val DOT_MATRIX_BOLD_RADIUS_RATIO = 0.57f
internal const val DOT_MATRIX_BOLD_OVERSCAN_RATIO =
    DOT_MATRIX_BOLD_RADIUS_RATIO - DOT_MATRIX_NORMAL_RADIUS_RATIO
internal const val DOT_MATRIX_ITALIC_OVERSCAN_RATIO = 1.8f
internal const val DOT_MATRIX_UNDERLINE_SPACING_RATIO = 0.3f
internal const val DOT_MATRIX_UNDERLINE_HEIGHT_RATIO = 1f

internal fun dotMatrixGlyphWidthUnits(glyph: DotMatrixGlyph): Float =
    glyph.width + (glyph.width - 1) * DOT_MATRIX_DOT_GAP_RATIO

internal fun dotMatrixGlyphHeightUnits(glyph: DotMatrixGlyph): Float =
    glyph.height + (glyph.height - 1) * DOT_MATRIX_DOT_GAP_RATIO

internal fun dotMatrixGlyphBaselineUnits(glyph: DotMatrixGlyph): Float =
    if (glyph.baseline == 0) 0f else {
        glyph.baseline + (glyph.baseline - 1) * DOT_MATRIX_DOT_GAP_RATIO
    }

internal fun dotMatrixItalicOffsetUnits(glyph: DotMatrixGlyph, row: Int): Float {
    require(row in 0 until glyph.height)
    return DOT_MATRIX_ITALIC_OVERSCAN_RATIO *
        (glyph.height - 1 - row) / (glyph.height - 1).coerceAtLeast(1)
}

internal fun dotMatrixBaselineAlignmentOffsetUnits(
    lineBaselineUnits: Float,
    glyph: DotMatrixGlyph,
): Float = lineBaselineUnits - dotMatrixGlyphBaselineUnits(glyph)

internal data class DotMatrixBlockSize(
    val width: Float,
    val height: Float,
)

internal data class DotMatrixStyleGeometry(
    val boldExtraWidthUnits: Float,
    val boldExtraHeightUnits: Float,
    val italicExtraWidthUnits: Float,
    val underlineExtraHeightUnits: Float,
) {
    val horizontalExtraWidthUnits: Float
        get() = boldExtraWidthUnits + italicExtraWidthUnits
}

internal data class DotMatrixLineLayout(
    val glyphs: List<DotMatrixGlyph>,
    val contentWidthUnits: Float,
    val contentHeightUnits: Float,
    val baselineUnits: Float,
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
    boldExtraWidthUnits = if (bold) DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f else 0f,
    boldExtraHeightUnits = if (bold) DOT_MATRIX_BOLD_OVERSCAN_RATIO * 2f else 0f,
    italicExtraWidthUnits = if (italic) DOT_MATRIX_ITALIC_OVERSCAN_RATIO else 0f,
    underlineExtraHeightUnits = if (underline) {
        DOT_MATRIX_UNDERLINE_SPACING_RATIO +
            if (bold) DOT_MATRIX_BOLD_RADIUS_RATIO * 2f else DOT_MATRIX_UNDERLINE_HEIGHT_RATIO
    } else {
        0f
    },
)

internal fun dotMatrixDotRadiusUnits(bold: Boolean): Float =
    if (bold) DOT_MATRIX_BOLD_RADIUS_RATIO else DOT_MATRIX_NORMAL_RADIUS_RATIO

internal fun calculateDotMatrixLineWidthUnits(
    glyphs: List<DotMatrixGlyph>,
    styleGeometry: DotMatrixStyleGeometry,
): Float {
    if (glyphs.isEmpty()) return 0f
    val baseWidth = glyphs.sumOf { dotMatrixGlyphWidthUnits(it).toDouble() }.toFloat() +
        (glyphs.size - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO
    return baseWidth + styleGeometry.horizontalExtraWidthUnits
}

internal fun createDotMatrixBlockLayout(
    message: String,
    glyphProvider: (Int) -> DotMatrixGlyph,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
): DotMatrixBlockLayout {
    val styleGeometry = calculateDotMatrixStyleGeometry(bold, italic, underline)
    val lines = splitDotMatrixLines(message).map { line ->
        val glyphs = line.codePoints().toArray().map(glyphProvider)
        val underlined = underline && glyphs.isNotEmpty()
        val emptyLineGlyph = if (glyphs.isEmpty()) glyphProvider(' '.code) else null
        // Blank glyphs retain horizontal advance, but do not reintroduce transparent font
        // line-box padding when the line also contains visible glyphs.
        val verticalMetricsGlyphs = glyphs.filter(DotMatrixGlyph::hasActivePixels)
            .ifEmpty { glyphs }
        val baselineUnits = verticalMetricsGlyphs.maxOfOrNull(::dotMatrixGlyphBaselineUnits)
            ?: dotMatrixGlyphBaselineUnits(requireNotNull(emptyLineGlyph))
        val descentUnits = verticalMetricsGlyphs.maxOfOrNull { glyph ->
            dotMatrixGlyphHeightUnits(glyph) - dotMatrixGlyphBaselineUnits(glyph)
        } ?: (dotMatrixGlyphHeightUnits(requireNotNull(emptyLineGlyph)) - baselineUnits)
        // Empty logical lines use the provider's space line box without adding a visible glyph.
        val contentHeightUnits = baselineUnits + descentUnits
        val contentWidthUnits = if (glyphs.isEmpty()) {
            0f
        } else {
            glyphs.sumOf { dotMatrixGlyphWidthUnits(it).toDouble() }.toFloat() +
                (glyphs.size - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO
        }
        DotMatrixLineLayout(
            glyphs = glyphs,
            contentWidthUnits = contentWidthUnits,
            contentHeightUnits = contentHeightUnits,
            baselineUnits = baselineUnits,
            widthUnits = calculateDotMatrixLineWidthUnits(glyphs, styleGeometry),
            heightUnits = contentHeightUnits +
                (if (glyphs.isNotEmpty()) styleGeometry.boldExtraHeightUnits else 0f) +
                (if (underlined) styleGeometry.underlineExtraHeightUnits else 0f),
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
    glyphProvider: (Int) -> DotMatrixGlyph,
    bold: Boolean = false,
    italic: Boolean = false,
    underline: Boolean = false,
): DotMatrixBlockSize {
    val layout = createDotMatrixBlockLayout(message, glyphProvider, bold, italic, underline)
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
