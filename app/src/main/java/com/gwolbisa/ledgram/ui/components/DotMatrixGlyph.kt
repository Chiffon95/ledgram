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

    fun hasActivePixels(): Boolean = rows.any { it != 0 }
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

/** Converts an ARGB bitmap to a glyph cropped to its active alpha bounds. */
internal fun croppedAlphaMaskGlyph(
    pixels: IntArray,
    width: Int,
    height: Int,
    baseline: Int,
    activeAlphaThreshold: Int,
): DotMatrixGlyph {
    require(width in 1..Int.SIZE_BITS)
    require(height > 0)
    require(pixels.size == width * height)
    require(baseline in 0..height)
    require(activeAlphaThreshold in 1..255)

    var activeLeft = width
    var activeTop = height
    var activeRight = -1
    var activeBottom = -1
    repeat(height) { row ->
        repeat(width) { column ->
            if (pixels[row * width + column].alpha() >= activeAlphaThreshold) {
                activeLeft = minOf(activeLeft, column)
                activeTop = minOf(activeTop, row)
                activeRight = maxOf(activeRight, column)
                activeBottom = maxOf(activeBottom, row)
            }
        }
    }

    if (activeRight < activeLeft || activeBottom < activeTop) {
        // Spaces keep their advance and common line box even though they have no active pixels.
        return DotMatrixGlyph(
            width = width,
            height = height,
            rows = List(height) { 0 },
            baseline = baseline,
        )
    }

    val croppedWidth = activeRight - activeLeft + 1
    val croppedHeight = activeBottom - activeTop + 1
    return DotMatrixGlyph(
        width = croppedWidth,
        height = croppedHeight,
        rows = List(croppedHeight) { croppedRow ->
            var mask = 0
            repeat(croppedWidth) { croppedColumn ->
                val sourceRow = activeTop + croppedRow
                val sourceColumn = activeLeft + croppedColumn
                val active = pixels[sourceRow * width + sourceColumn].alpha() >=
                    activeAlphaThreshold
                mask = (mask shl 1) or if (active) 1 else 0
            }
            mask
        },
        baseline = (baseline - activeTop).coerceIn(0, croppedHeight),
    )
}

private fun Int.alpha(): Int = ushr(24) and 0xFF

internal fun interface DotMatrixGlyphProvider {
    fun glyphFor(codePoint: Int): DotMatrixGlyph
}

internal fun splitDotMatrixLines(message: String): List<String> =
    message.split(delimiters = charArrayOf('\n'), ignoreCase = false, limit = Int.MAX_VALUE)
