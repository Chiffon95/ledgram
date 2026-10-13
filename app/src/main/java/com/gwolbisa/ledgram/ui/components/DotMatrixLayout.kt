package com.gwolbisa.ledgram.ui.components

import kotlin.math.min

internal const val DOT_MATRIX_DOT_GAP_RATIO = 0.3f
internal const val DOT_MATRIX_CHARACTER_GAP_RATIO = 1.3f
internal const val DOT_MATRIX_LINE_GAP_RATIO = 1.3f

internal data class DotMatrixBlockSize(
    val width: Float,
    val height: Float,
)

internal data class DotMatrixLineLayout(
    val glyphs: List<DotMatrixGlyph>,
    val widthUnits: Float,
)

internal data class DotMatrixBlockLayout(
    val lines: List<DotMatrixLineLayout>,
    val widthUnits: Float,
    val heightUnits: Float,
)

internal fun calculateDotMatrixBlockSize(
    message: String,
    dotSize: Float,
    dotGap: Float = dotSize * DOT_MATRIX_DOT_GAP_RATIO,
    characterGap: Float = dotSize * DOT_MATRIX_CHARACTER_GAP_RATIO,
    lineGap: Float = dotSize * DOT_MATRIX_LINE_GAP_RATIO,
): DotMatrixBlockSize {
    val lines = splitDotMatrixLines(message)
    val glyphWidth = DOT_MATRIX_GLYPH_WIDTH * dotSize +
        (DOT_MATRIX_GLYPH_WIDTH - 1) * dotGap
    val glyphHeight = DOT_MATRIX_GLYPH_HEIGHT * dotSize +
        (DOT_MATRIX_GLYPH_HEIGHT - 1) * dotGap
    val lineWidths = lines.map { line ->
        val glyphCount = line.codePointCount(0, line.length)
        if (glyphCount == 0) 0f else {
            glyphCount * glyphWidth + (glyphCount - 1) * characterGap
        }
    }
    return DotMatrixBlockSize(
        width = lineWidths.maxOrNull() ?: 0f,
        height = lines.size * glyphHeight + (lines.size - 1).coerceAtLeast(0) * lineGap,
    )
}

internal fun createDotMatrixBlockLayout(message: String): DotMatrixBlockLayout {
    val unitSize = calculateDotMatrixBlockSize(message = message, dotSize = 1f)
    val lines = splitDotMatrixLines(message).map { line ->
        val glyphs = line.codePoints().toArray().map(::dotMatrixGlyphFor)
        val width = if (glyphs.isEmpty()) {
            0f
        } else {
            val glyphWidth = DOT_MATRIX_GLYPH_WIDTH +
                (DOT_MATRIX_GLYPH_WIDTH - 1) * DOT_MATRIX_DOT_GAP_RATIO
            glyphs.size * glyphWidth +
                (glyphs.size - 1) * DOT_MATRIX_CHARACTER_GAP_RATIO
        }
        DotMatrixLineLayout(glyphs = glyphs, widthUnits = width)
    }
    return DotMatrixBlockLayout(
        lines = lines,
        widthUnits = unitSize.width,
        heightUnits = unitSize.height,
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
