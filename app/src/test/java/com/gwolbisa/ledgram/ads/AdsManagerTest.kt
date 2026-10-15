package com.gwolbisa.ledgram.ads

import org.junit.Assert.assertEquals
import org.junit.Test

class AdsManagerTest {
    @Test
    fun `test device configuration ignores blanks and duplicate IDs`() {
        assertEquals(
            listOf("device-one", "device-two"),
            parseTestDeviceIds(" device-one, ,device-two,device-one "),
        )
    }

    @Test
    fun `empty test device configuration disables test device overrides`() {
        assertEquals(emptyList<String>(), parseTestDeviceIds("  "))
    }
}
