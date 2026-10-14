package com.gwolbisa.ledgram.ui.components

internal data class DotMatrixGlyph(
    val width: Int,
    val height: Int,
    val rows: List<Int>,
    val baseline: Int = height,
) {
    init {
        require(width in 1..Int.SIZE_BITS)
        require(height > 0)
        require(rows.size == height)
        require(baseline in 0..height)
    }

    fun isActive(row: Int, column: Int): Boolean =
        rows[row] and (1 shl (width - 1 - column)) != 0
}

internal fun dotMatrixGlyph(
    vararg rows: String,
    baseline: Int = rows.size,
): DotMatrixGlyph {
    require(rows.isNotEmpty())
    val width = rows.first().length
    require(width > 0 && rows.all { it.length == width })
    return DotMatrixGlyph(
        width = width,
        height = rows.size,
        rows = rows.map { row ->
            row.fold(0) { mask, pixel -> (mask shl 1) or if (pixel == '#') 1 else 0 }
        },
        baseline = baseline,
    )
}

internal val FALLBACK_GLYPH = dotMatrixGlyph(
    ".###.",
    "#...#",
    "....#",
    "...#.",
    "..#..",
    ".....",
    "..#..",
)

internal fun interface DotMatrixGlyphProvider {
    fun glyphFor(codePoint: Int): DotMatrixGlyph
}

internal fun splitDotMatrixLines(message: String): List<String> =
    message.split(delimiters = charArrayOf('\n'), ignoreCase = false, limit = Int.MAX_VALUE)
