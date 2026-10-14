package com.gwolbisa.ledgram.ui.components

internal const val DOT_MATRIX_GLYPH_WIDTH = 5
internal const val DOT_MATRIX_GLYPH_HEIGHT = 7

// Hangul needs more room than the compact Latin font. Every composed modern syllable uses this
// deterministic 11 x 11 grid; Latin, digits, and punctuation keep their existing 5 x 7 cells.
internal const val DOT_MATRIX_HANGUL_GLYPH_WIDTH = 11
internal const val DOT_MATRIX_HANGUL_GLYPH_HEIGHT = 11

internal data class DotMatrixGlyph(
    val width: Int,
    val height: Int,
    val rows: List<Int>,
) {
    init {
        require(width in 1..Int.SIZE_BITS)
        require(height > 0)
        require(rows.size == height)
    }

    fun isActive(row: Int, column: Int): Boolean =
        rows[row] and (1 shl (width - 1 - column)) != 0
}

private fun glyph(vararg rows: String): DotMatrixGlyph {
    require(rows.isNotEmpty())
    val width = rows.first().length
    require(width > 0 && rows.all { it.length == width })
    return DotMatrixGlyph(
        width = width,
        height = rows.size,
        rows = rows.map { row ->
            row.fold(0) { mask, pixel -> (mask shl 1) or if (pixel == '#') 1 else 0 }
        },
    )
}

internal val FALLBACK_GLYPH = glyph(
    ".###.", "#...#", "....#", "...#.", "..#..", ".....", "..#..",
)

private val DOT_MATRIX_GLYPHS: Map<Char, DotMatrixGlyph> = mapOf(
    ' ' to glyph(".....", ".....", ".....", ".....", ".....", ".....", "....."),
    'A' to glyph(".###.", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
    'B' to glyph("####.", "#...#", "#...#", "####.", "#...#", "#...#", "####."),
    'C' to glyph(".####", "#....", "#....", "#....", "#....", "#....", ".####"),
    'D' to glyph("####.", "#...#", "#...#", "#...#", "#...#", "#...#", "####."),
    'E' to glyph("#####", "#....", "#....", "####.", "#....", "#....", "#####"),
    'F' to glyph("#####", "#....", "#....", "####.", "#....", "#....", "#...."),
    'G' to glyph(".####", "#....", "#....", "#.###", "#...#", "#...#", ".###."),
    'H' to glyph("#...#", "#...#", "#...#", "#####", "#...#", "#...#", "#...#"),
    'I' to glyph("#####", "..#..", "..#..", "..#..", "..#..", "..#..", "#####"),
    'J' to glyph("..###", "...#.", "...#.", "...#.", "...#.", "#..#.", ".##.."),
    'K' to glyph("#...#", "#..#.", "#.#..", "##...", "#.#..", "#..#.", "#...#"),
    'L' to glyph("#....", "#....", "#....", "#....", "#....", "#....", "#####"),
    'M' to glyph("#...#", "##.##", "#.#.#", "#...#", "#...#", "#...#", "#...#"),
    'N' to glyph("#...#", "##..#", "#.#.#", "#..##", "#...#", "#...#", "#...#"),
    'O' to glyph(".###.", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
    'P' to glyph("####.", "#...#", "#...#", "####.", "#....", "#....", "#...."),
    'Q' to glyph(".###.", "#...#", "#...#", "#...#", "#.#.#", "#..#.", ".##.#"),
    'R' to glyph("####.", "#...#", "#...#", "####.", "#.#..", "#..#.", "#...#"),
    'S' to glyph(".####", "#....", "#....", ".###.", "....#", "....#", "####."),
    'T' to glyph("#####", "..#..", "..#..", "..#..", "..#..", "..#..", "..#.."),
    'U' to glyph("#...#", "#...#", "#...#", "#...#", "#...#", "#...#", ".###."),
    'V' to glyph("#...#", "#...#", "#...#", "#...#", "#...#", ".#.#.", "..#.."),
    'W' to glyph("#...#", "#...#", "#...#", "#.#.#", "#.#.#", "##.##", "#...#"),
    'X' to glyph("#...#", "#...#", ".#.#.", "..#..", ".#.#.", "#...#", "#...#"),
    'Y' to glyph("#...#", "#...#", ".#.#.", "..#..", "..#..", "..#..", "..#.."),
    'Z' to glyph("#####", "....#", "...#.", "..#..", ".#...", "#....", "#####"),
    '0' to glyph(".###.", "#...#", "#..##", "#.#.#", "##..#", "#...#", ".###."),
    '1' to glyph("..#..", ".##..", "..#..", "..#..", "..#..", "..#..", ".###."),
    '2' to glyph(".###.", "#...#", "....#", "...#.", "..#..", ".#...", "#####"),
    '3' to glyph("####.", "....#", "....#", ".###.", "....#", "....#", "####."),
    '4' to glyph("...#.", "..##.", ".#.#.", "#..#.", "#####", "...#.", "...#."),
    '5' to glyph("#####", "#....", "#....", "####.", "....#", "....#", "####."),
    '6' to glyph(".###.", "#....", "#....", "####.", "#...#", "#...#", ".###."),
    '7' to glyph("#####", "....#", "...#.", "..#..", ".#...", ".#...", ".#..."),
    '8' to glyph(".###.", "#...#", "#...#", ".###.", "#...#", "#...#", ".###."),
    '9' to glyph(".###.", "#...#", "#...#", ".####", "....#", "....#", ".###."),
    '.' to glyph(".....", ".....", ".....", ".....", ".....", "..##.", "..##."),
    ',' to glyph(".....", ".....", ".....", ".....", "..##.", "..##.", ".##.."),
    '!' to glyph("..#..", "..#..", "..#..", "..#..", "..#..", ".....", "..#.."),
    '?' to FALLBACK_GLYPH,
    ':' to glyph(".....", "..#..", "..#..", ".....", "..#..", "..#..", "....."),
    ';' to glyph(".....", "..#..", "..#..", ".....", "..#..", "..#..", ".#..."),
    '-' to glyph(".....", ".....", ".....", ".###.", ".....", ".....", "....."),
    '_' to glyph(".....", ".....", ".....", ".....", ".....", ".....", "#####"),
    '+' to glyph(".....", "..#..", "..#..", "#####", "..#..", "..#..", "....."),
    '/' to glyph("....#", "...#.", "...#.", "..#..", ".#...", ".#...", "#...."),
    '\\' to glyph("#....", ".#...", ".#...", "..#..", "...#.", "...#.", "....#"),
    '(' to glyph("...#.", "..#..", ".#...", ".#...", ".#...", "..#..", "...#."),
    ')' to glyph(".#...", "..#..", "...#.", "...#.", "...#.", "..#..", ".#..."),
    '[' to glyph(".###.", ".#...", ".#...", ".#...", ".#...", ".#...", ".###."),
    ']' to glyph(".###.", "...#.", "...#.", "...#.", "...#.", "...#.", ".###."),
    '#' to glyph(".#.#.", ".#.#.", "#####", ".#.#.", "#####", ".#.#.", ".#.#."),
    '@' to glyph(".###.", "#...#", "#.###", "#.#.#", "#.###", "#....", ".####"),
)

internal enum class HangulVowelLayout { VERTICAL, HORIZONTAL, COMPOUND }

internal data class HangulSyllable(
    val initialIndex: Int,
    val vowelIndex: Int,
    val finalIndex: Int,
    val vowelLayout: HangulVowelLayout,
) {
    val hasFinal: Boolean get() = finalIndex != 0
}

internal fun decomposeHangulSyllable(codePoint: Int): HangulSyllable? {
    if (codePoint !in 0xAC00..0xD7A3) return null
    val syllableIndex = codePoint - 0xAC00
    val vowelIndex = (syllableIndex % 588) / 28
    return HangulSyllable(
        initialIndex = syllableIndex / 588,
        vowelIndex = vowelIndex,
        finalIndex = syllableIndex % 28,
        vowelLayout = when (vowelIndex) {
            in 0..7, 20 -> HangulVowelLayout.VERTICAL
            8, 12, 13, 17, 18 -> HangulVowelLayout.HORIZONTAL
            else -> HangulVowelLayout.COMPOUND
        },
    )
}

private enum class Consonant {
    GIYEOK, NIEUN, DIGEUT, RIEUL, MIEUM, BIEUP, SIOT, IEUNG, JIEUT, CHIEUT,
    KIEUK, TIEUT, PIEUP, HIEUH,
}

private data class Point(val x: Int, val y: Int)
private data class Stroke(val points: List<Point>)

private fun stroke(vararg coordinates: Int): Stroke {
    require(coordinates.size >= 4 && coordinates.size % 2 == 0)
    return Stroke(coordinates.toList().chunked(2).map { Point(it[0], it[1]) })
}

// Reusable modern-Jamo stroke templates use a normalized 0..4 component space.
private val CONSONANT_STROKES = mapOf(
    Consonant.GIYEOK to listOf(stroke(0, 0, 4, 0, 4, 4)),
    Consonant.NIEUN to listOf(stroke(0, 0, 0, 4, 4, 4)),
    Consonant.DIGEUT to listOf(stroke(0, 0, 4, 0, 4, 4, 0, 4)),
    Consonant.RIEUL to listOf(stroke(0, 0, 4, 0, 4, 2, 0, 2, 0, 4, 4, 4)),
    Consonant.MIEUM to listOf(stroke(0, 0, 4, 0, 4, 4, 0, 4, 0, 0)),
    Consonant.BIEUP to listOf(stroke(0, 0, 0, 4), stroke(4, 0, 4, 4), stroke(0, 0, 4, 0), stroke(0, 2, 4, 2), stroke(0, 4, 4, 4)),
    Consonant.SIOT to listOf(stroke(2, 0, 0, 4), stroke(2, 0, 4, 4)),
    Consonant.IEUNG to listOf(stroke(1, 0, 3, 0, 4, 1, 4, 3, 3, 4, 1, 4, 0, 3, 0, 1, 1, 0)),
    Consonant.JIEUT to listOf(stroke(0, 0, 4, 0), stroke(2, 1, 0, 4), stroke(2, 1, 4, 4)),
    Consonant.CHIEUT to listOf(stroke(1, 0, 3, 0), stroke(0, 2, 4, 2), stroke(2, 2, 0, 4), stroke(2, 2, 4, 4)),
    Consonant.KIEUK to listOf(stroke(0, 0, 4, 0, 4, 4), stroke(1, 2, 4, 2)),
    Consonant.TIEUT to listOf(stroke(0, 0, 4, 0), stroke(0, 2, 4, 2), stroke(0, 4, 4, 4), stroke(1, 0, 1, 4), stroke(3, 0, 3, 4)),
    Consonant.PIEUP to listOf(stroke(0, 0, 0, 4), stroke(4, 0, 4, 4), stroke(0, 1, 4, 1), stroke(0, 3, 4, 3)),
    Consonant.HIEUH to listOf(stroke(1, 0, 3, 0), stroke(0, 1, 4, 1), stroke(1, 2, 3, 2, 4, 3, 3, 4, 1, 4, 0, 3, 1, 2)),
)

private val INITIAL_COMPONENTS = listOf(
    listOf(Consonant.GIYEOK), listOf(Consonant.GIYEOK, Consonant.GIYEOK), listOf(Consonant.NIEUN),
    listOf(Consonant.DIGEUT), listOf(Consonant.DIGEUT, Consonant.DIGEUT), listOf(Consonant.RIEUL),
    listOf(Consonant.MIEUM), listOf(Consonant.BIEUP), listOf(Consonant.BIEUP, Consonant.BIEUP),
    listOf(Consonant.SIOT), listOf(Consonant.SIOT, Consonant.SIOT), listOf(Consonant.IEUNG),
    listOf(Consonant.JIEUT), listOf(Consonant.JIEUT, Consonant.JIEUT), listOf(Consonant.CHIEUT),
    listOf(Consonant.KIEUK), listOf(Consonant.TIEUT), listOf(Consonant.PIEUP), listOf(Consonant.HIEUH),
)

private val FINAL_COMPONENTS: List<List<Consonant>> = listOf(
    emptyList(), listOf(Consonant.GIYEOK), listOf(Consonant.GIYEOK, Consonant.GIYEOK),
    listOf(Consonant.GIYEOK, Consonant.SIOT), listOf(Consonant.NIEUN),
    listOf(Consonant.NIEUN, Consonant.JIEUT), listOf(Consonant.NIEUN, Consonant.HIEUH),
    listOf(Consonant.DIGEUT), listOf(Consonant.RIEUL), listOf(Consonant.RIEUL, Consonant.GIYEOK),
    listOf(Consonant.RIEUL, Consonant.MIEUM), listOf(Consonant.RIEUL, Consonant.BIEUP),
    listOf(Consonant.RIEUL, Consonant.SIOT), listOf(Consonant.RIEUL, Consonant.TIEUT),
    listOf(Consonant.RIEUL, Consonant.PIEUP), listOf(Consonant.RIEUL, Consonant.HIEUH),
    listOf(Consonant.MIEUM), listOf(Consonant.BIEUP), listOf(Consonant.BIEUP, Consonant.SIOT),
    listOf(Consonant.SIOT), listOf(Consonant.SIOT, Consonant.SIOT), listOf(Consonant.IEUNG),
    listOf(Consonant.JIEUT), listOf(Consonant.CHIEUT), listOf(Consonant.KIEUK),
    listOf(Consonant.TIEUT), listOf(Consonant.PIEUP), listOf(Consonant.HIEUH),
)

private class GlyphCanvas(val width: Int, val height: Int) {
    private val cells = Array(height) { BooleanArray(width) }

    fun line(startX: Int, startY: Int, endX: Int, endY: Int) {
        var x = startX
        var y = startY
        val dx = kotlin.math.abs(endX - startX)
        val sx = if (startX < endX) 1 else -1
        val dy = -kotlin.math.abs(endY - startY)
        val sy = if (startY < endY) 1 else -1
        var error = dx + dy
        while (true) {
            if (x in 0 until width && y in 0 until height) cells[y][x] = true
            if (x == endX && y == endY) break
            val doubled = error * 2
            if (doubled >= dy) { error += dy; x += sx }
            if (doubled <= dx) { error += dx; y += sy }
        }
    }

    fun toGlyph(): DotMatrixGlyph = DotMatrixGlyph(
        width = width,
        height = height,
        rows = cells.map { row -> row.fold(0) { mask, active -> (mask shl 1) or if (active) 1 else 0 } },
    )
}

private data class Bounds(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun GlyphCanvas.drawComponents(components: List<Consonant>, bounds: Bounds) {
    if (components.isEmpty()) return
    val gap = if (components.size > 1) 1 else 0
    val availableWidth = bounds.right - bounds.left + 1 - gap * (components.size - 1)
    components.forEachIndexed { index, component ->
        val start = bounds.left + index * availableWidth / components.size + index * gap
        val end = bounds.left + (index + 1) * availableWidth / components.size - 1 + index * gap
        CONSONANT_STROKES.getValue(component).forEach { normalizedStroke ->
            val points = normalizedStroke.points.map { point ->
                Point(
                    x = start + point.x * (end - start).coerceAtLeast(0) / 4,
                    y = bounds.top + point.y * (bounds.bottom - bounds.top).coerceAtLeast(0) / 4,
                )
            }
            points.zipWithNext().forEach { (from, to) -> line(from.x, from.y, to.x, to.y) }
        }
    }
}

private fun GlyphCanvas.drawVowel(vowelIndex: Int, bounds: Bounds) {
    fun x(value: Int) = bounds.left + value * (bounds.right - bounds.left) / 4
    fun y(value: Int) = bounds.top + value * (bounds.bottom - bounds.top) / 4
    fun vertical(column: Int) = line(x(column), y(0), x(column), y(4))
    fun horizontal(row: Int) = line(x(0), y(row), x(4), y(row))
    fun rightArm(row: Int, from: Int = 2) = line(x(from), y(row), x(4), y(row))
    fun leftArm(row: Int, to: Int = 2) = line(x(0), y(row), x(to), y(row))

    when (vowelIndex) {
        0 -> { vertical(2); rightArm(2) }
        1 -> { vertical(1); rightArm(2, 1); vertical(4) }
        2 -> { vertical(2); rightArm(1); rightArm(3) }
        3 -> { vertical(1); rightArm(1, 1); rightArm(3, 1); vertical(4) }
        4 -> { vertical(2); leftArm(2) }
        5 -> { vertical(3); leftArm(2, 3); vertical(0) }
        6 -> { vertical(2); leftArm(1); leftArm(3) }
        7 -> { vertical(3); leftArm(1, 3); leftArm(3, 3); vertical(0) }
        8 -> { horizontal(3); line(x(2), y(0), x(2), y(3)) }
        9 -> { horizontal(3); line(x(1), y(0), x(1), y(3)); vertical(3); rightArm(2, 3) }
        10 -> { horizontal(3); line(x(1), y(0), x(1), y(3)); vertical(3); rightArm(2, 3); vertical(4) }
        11 -> { horizontal(3); line(x(1), y(0), x(1), y(3)); vertical(4) }
        12 -> { horizontal(3); line(x(1), y(0), x(1), y(3)); line(x(3), y(0), x(3), y(3)) }
        13 -> { horizontal(1); line(x(2), y(1), x(2), y(4)) }
        14 -> { horizontal(1); line(x(1), y(1), x(1), y(4)); vertical(3); leftArm(2, 3) }
        15 -> { horizontal(1); line(x(1), y(1), x(1), y(4)); vertical(3); leftArm(2, 3); vertical(4) }
        16 -> { horizontal(1); line(x(1), y(1), x(1), y(4)); vertical(4) }
        17 -> { horizontal(1); line(x(1), y(1), x(1), y(4)); line(x(3), y(1), x(3), y(4)) }
        18 -> horizontal(2)
        19 -> { horizontal(2); vertical(4) }
        20 -> vertical(2)
    }
}

private fun composeHangulGlyph(syllable: HangulSyllable): DotMatrixGlyph {
    val canvas = GlyphCanvas(DOT_MATRIX_HANGUL_GLYPH_WIDTH, DOT_MATRIX_HANGUL_GLYPH_HEIGHT)
    val topBottom = if (syllable.hasFinal) 6 else 10
    when (syllable.vowelLayout) {
        HangulVowelLayout.VERTICAL -> {
            canvas.drawComponents(INITIAL_COMPONENTS[syllable.initialIndex], Bounds(0, 0, 5, topBottom))
            canvas.drawVowel(syllable.vowelIndex, Bounds(6, 0, 10, topBottom))
        }
        HangulVowelLayout.HORIZONTAL -> {
            val initialBottom = if (syllable.hasFinal) 3 else 5
            canvas.drawComponents(INITIAL_COMPONENTS[syllable.initialIndex], Bounds(0, 0, 10, initialBottom))
            canvas.drawVowel(syllable.vowelIndex, Bounds(0, initialBottom + 1, 10, topBottom))
        }
        HangulVowelLayout.COMPOUND -> {
            val initialBottom = if (syllable.hasFinal) 3 else 5
            canvas.drawComponents(INITIAL_COMPONENTS[syllable.initialIndex], Bounds(0, 0, 5, initialBottom))
            canvas.drawVowel(syllable.vowelIndex, Bounds(0, initialBottom + 1, 10, topBottom))
        }
    }
    if (syllable.hasFinal) {
        canvas.drawComponents(FINAL_COMPONENTS[syllable.finalIndex], Bounds(0, 7, 10, 10))
    }
    return canvas.toGlyph()
}

internal fun dotMatrixGlyphFor(character: Char): DotMatrixGlyph = dotMatrixGlyphFor(character.code)

internal fun dotMatrixGlyphFor(codePoint: Int): DotMatrixGlyph {
    decomposeHangulSyllable(codePoint)?.let { return composeHangulGlyph(it) }
    if (!Character.isBmpCodePoint(codePoint)) return FALLBACK_GLYPH
    return DOT_MATRIX_GLYPHS[codePoint.toChar().uppercaseChar()] ?: FALLBACK_GLYPH
}

internal fun splitDotMatrixLines(message: String): List<String> =
    message.split(delimiters = charArrayOf('\n'), ignoreCase = false, limit = Int.MAX_VALUE)
