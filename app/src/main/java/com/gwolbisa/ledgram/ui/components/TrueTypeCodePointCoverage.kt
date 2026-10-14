package com.gwolbisa.ledgram.ui.components

import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Reads Unicode coverage from the bundled font's cmap table without consulting device fonts. */
internal class TrueTypeCodePointCoverage private constructor(
    private val ranges: List<IntRange>,
) {
    fun contains(codePoint: Int): Boolean {
        var low = 0
        var high = ranges.lastIndex
        while (low <= high) {
            val middle = (low + high).ushr(1)
            val range = ranges[middle]
            when {
                codePoint < range.first -> high = middle - 1
                codePoint > range.last -> low = middle + 1
                else -> return true
            }
        }
        return false
    }

    companion object {
        fun fromTrueType(fontBytes: ByteArray): TrueTypeCodePointCoverage {
            val buffer = ByteBuffer.wrap(fontBytes).order(ByteOrder.BIG_ENDIAN)
            val tableCount = buffer.unsignedShort(4)
            val cmapOffset = (0 until tableCount)
                .asSequence()
                .map { 12 + it * 16 }
                .firstOrNull { buffer.tag(it) == "cmap" }
                ?.let { buffer.unsignedInt(it + 8) }
                ?: error("Bundled font has no cmap table")
            val subtableCount = buffer.unsignedShort(cmapOffset + 2)
            val subtables = (0 until subtableCount).map { index ->
                val record = cmapOffset + 4 + index * 8
                val platform = buffer.unsignedShort(record)
                val encoding = buffer.unsignedShort(record + 2)
                val offset = cmapOffset + buffer.unsignedInt(record + 4)
                CmapSubtable(platform, encoding, offset, buffer.unsignedShort(offset))
            }
            val selected = subtables.firstOrNull {
                it.format == 12 && (it.platform == 0 || it.platform == 3 && it.encoding == 10)
            } ?: subtables.firstOrNull {
                it.format == 4 && (it.platform == 0 || it.platform == 3 && it.encoding == 1)
            } ?: error("Bundled font has no supported Unicode cmap")
            val ranges = when (selected.format) {
                12 -> parseFormat12(buffer, selected.offset)
                4 -> parseFormat4(buffer, selected.offset)
                else -> error("Unsupported cmap format ${selected.format}")
            }
            return TrueTypeCodePointCoverage(ranges)
        }

        private fun parseFormat12(buffer: ByteBuffer, offset: Int): List<IntRange> {
            val groupCount = buffer.unsignedInt(offset + 12)
            return buildList {
                repeat(groupCount) { index ->
                    val group = offset + 16 + index * 12
                    var start = buffer.unsignedInt(group)
                    val end = buffer.unsignedInt(group + 4)
                    val firstGlyph = buffer.unsignedInt(group + 8)
                    if (firstGlyph == 0) start++
                    if (start <= end) add(start..end)
                }
            }
        }

        private fun parseFormat4(buffer: ByteBuffer, offset: Int): List<IntRange> {
            val segmentCount = buffer.unsignedShort(offset + 6) / 2
            val endCodesOffset = offset + 14
            val startCodesOffset = endCodesOffset + segmentCount * 2 + 2
            val deltasOffset = startCodesOffset + segmentCount * 2
            val rangeOffsetsOffset = deltasOffset + segmentCount * 2
            val supported = mutableListOf<Int>()
            repeat(segmentCount) { index ->
                val start = buffer.unsignedShort(startCodesOffset + index * 2)
                val end = buffer.unsignedShort(endCodesOffset + index * 2)
                val delta = buffer.getShort(deltasOffset + index * 2).toInt()
                val rangeOffsetPosition = rangeOffsetsOffset + index * 2
                val rangeOffset = buffer.unsignedShort(rangeOffsetPosition)
                for (codePoint in start..end) {
                    if (codePoint == 0xFFFF) continue
                    val glyphId = if (rangeOffset == 0) {
                        (codePoint + delta) and 0xFFFF
                    } else {
                        val glyphAddress = rangeOffsetPosition + rangeOffset +
                            (codePoint - start) * 2
                        val rawGlyph = buffer.unsignedShort(glyphAddress)
                        if (rawGlyph == 0) 0 else (rawGlyph + delta) and 0xFFFF
                    }
                    if (glyphId != 0) supported += codePoint
                }
            }
            if (supported.isEmpty()) return emptyList()
            return buildList {
                var rangeStart = supported.first()
                var previous = rangeStart
                supported.drop(1).forEach { codePoint ->
                    if (codePoint != previous + 1) {
                        add(rangeStart..previous)
                        rangeStart = codePoint
                    }
                    previous = codePoint
                }
                add(rangeStart..previous)
            }
        }
    }
}

private data class CmapSubtable(
    val platform: Int,
    val encoding: Int,
    val offset: Int,
    val format: Int,
)

private fun ByteBuffer.unsignedShort(offset: Int): Int = getShort(offset).toInt() and 0xFFFF

private fun ByteBuffer.unsignedInt(offset: Int): Int {
    val value = getInt(offset).toLong() and 0xFFFF_FFFFL
    require(value <= Int.MAX_VALUE) { "Font table offset is too large" }
    return value.toInt()
}

private fun ByteBuffer.tag(offset: Int): String = buildString(4) {
    repeat(4) { append(this@tag.get(offset + it).toInt().toChar()) }
}
