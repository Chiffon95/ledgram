package com.gwolbisa.ledgram.model

fun DisplayEffect.isTextGlowEffectImplemented(): Boolean = when (this) {
    DisplayEffect.STATIC,
    DisplayEffect.BLINK,
    DisplayEffect.PULSE,
    DisplayEffect.FADE -> true

    DisplayEffect.RIGHT_TO_LEFT,
    DisplayEffect.LEFT_TO_RIGHT -> false
}

fun DisplayEffect.forCurrentRuntime(): DisplayEffect =
    if (isTextGlowEffectImplemented()) this else DisplayEffect.STATIC
