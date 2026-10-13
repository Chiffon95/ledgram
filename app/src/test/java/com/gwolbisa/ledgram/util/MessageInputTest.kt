package com.gwolbisa.ledgram.util

import com.gwolbisa.ledgram.model.MotionEffect
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageInputTest {
    @Test
    fun `one newline is preserved and line endings are canonicalized`() {
        assertEquals("HELLO\nWORLD", normalizeMessageInput("HELLO\nWORLD"))
        assertEquals("HELLO\nWORLD", normalizeMessageInput("HELLO\r\nWORLD"))
        assertEquals("HELLO\nWORLD", normalizeMessageInput("HELLO\rWORLD"))
    }

    @Test
    fun `subsequent and repeated newlines become a single separating space`() {
        assertEquals("A\nB C", normalizeMessageInput("A\nB\nC"))
        assertEquals("A\nB C", normalizeMessageInput("A\nB\n\nC"))
        assertEquals("A\nB", normalizeMessageInput("A\n\nB"))
    }

    @Test
    fun `input is limited without splitting supplementary unicode characters`() {
        val emoji = "😀"
        val input = emoji.repeat(MAX_MESSAGE_LENGTH + 1)

        val result = normalizeMessageInput(input)

        assertEquals(MAX_MESSAGE_LENGTH, result.codePointLength())
        assertEquals(emoji.repeat(MAX_MESSAGE_LENGTH), result)
    }

    @Test
    fun `short unicode input is unchanged`() {
        assertEquals("안녕하세요 👋", normalizeMessageInput("안녕하세요 👋"))
    }

    @Test
    fun `newline counts toward code point limit without splitting emoji`() {
        val prefix = "A".repeat(98)

        assertEquals(
            prefix + "\n😀",
            normalizeMessageInput(prefix + "\n😀X"),
        )
        assertEquals(MAX_MESSAGE_LENGTH, normalizeMessageInput(prefix + "\n😀X").codePointLength())
        assertEquals("A".repeat(99) + "\n", normalizeMessageInput("A".repeat(99) + "\nB"))
    }

    @Test
    fun `display normalization is motion aware`() {
        val input = "  HELLO\nWORLD  "

        assertEquals("HELLO\nWORLD", normalizeMessageForStaticDisplay(input))
        assertEquals("HELLO WORLD", normalizeMessageForMarqueeDisplay(input))
        assertEquals("HELLO\nWORLD", normalizeMessageForDisplay(input, MotionEffect.STATIC))
        assertEquals(
            "HELLO WORLD",
            normalizeMessageForDisplay(input, MotionEffect.RIGHT_TO_LEFT),
        )
        assertEquals(
            "HELLO WORLD",
            normalizeMessageForDisplay(input, MotionEffect.LEFT_TO_RIGHT),
        )
    }

    @Test
    fun `whitespace and newline only display messages are blank`() {
        listOf("", "   ", "\n", " \n ").forEach { input ->
            assertEquals("", normalizeMessageForStaticDisplay(input))
            assertEquals("", normalizeMessageForMarqueeDisplay(input))
        }
    }
}

