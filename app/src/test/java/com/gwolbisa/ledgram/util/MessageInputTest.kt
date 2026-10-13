package com.gwolbisa.ledgram.util

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageInputTest {
    @Test
    fun `all newlines are preserved and line endings are canonicalized`() {
        assertEquals("A\nB\nC", normalizeMessageInput("A\nB\nC"))
        assertEquals("A\nB\nC\nD", normalizeMessageInput("A\r\nB\rC\nD"))
    }

    @Test
    fun `repeated newlines remain stored and rendered`() {
        val input = "HELLO\n\nWORLD\n\nLEDGRAM"

        assertEquals(input, normalizeMessageInput(input))
        assertEquals(input, normalizeMessageForDisplay(input))
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
    fun `one hundred code point multiline message is preserved`() {
        val input = ("A\n".repeat(49)) + "😀B"

        assertEquals(MAX_MESSAGE_LENGTH, input.codePointLength())
        assertEquals(input, normalizeMessageInput(input))
    }

    @Test
    fun `display normalization preserves multiline structure`() {
        val input = "  HELLO\nWORLD\nLEDGRAM  "

        assertEquals("HELLO\nWORLD\nLEDGRAM", normalizeMessageForDisplay(input))
    }

    @Test
    fun `display preserves leading and trailing explicit newlines`() {
        val input = " \nHELLO\nWORLD\n "

        assertEquals("\nHELLO\nWORLD\n", normalizeMessageForDisplay(input))
    }

    @Test
    fun `whitespace and newline only display messages are blank`() {
        listOf("", "   ", "\n", "\n\n", " \n \n \t").forEach { input ->
            assertEquals("", normalizeMessageForDisplay(input))
        }
    }
}

