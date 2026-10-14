package com.gwolbisa.ledgram.ui.components

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrueTypeCodePointCoverageTest {
    @Test
    fun `bundled Galmuri font covers case Hangul and compatibility Jamo`() {
        val fontFile = File("src/main/res/font/galmuri11.ttf")
        assertTrue("Bundled Galmuri11 resource is missing", fontFile.isFile)
        val coverage = TrueTypeCodePointCoverage.fromTrueType(fontFile.readBytes())

        "Aa가한글안녕하세요대한민국응원최고ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅎㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣ"
            .codePoints()
            .forEach { codePoint ->
                assertTrue(
                    "Missing glyph for ${String(Character.toChars(codePoint))}",
                    coverage.contains(codePoint),
                )
            }
        (0xAC00..0xD7A3).forEach { codePoint ->
            assertTrue("Missing modern Hangul U+${codePoint.toString(16)}", coverage.contains(codePoint))
        }
        (0x3131..0x318E).forEach { codePoint ->
            assertTrue("Missing compatibility Jamo U+${codePoint.toString(16)}", coverage.contains(codePoint))
        }
        assertFalse(coverage.contains(0x1F600))
    }
}
