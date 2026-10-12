package com.gwolbisa.ledgram.util

import org.junit.Assert.assertEquals
import org.junit.Test

class MessageInputTest {
    @Test
    fun `line breaks become a single space`() {
        assertEquals("Hello world again", normalizeMessageInput("Hello\nworld\r\nagain"))
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
}

