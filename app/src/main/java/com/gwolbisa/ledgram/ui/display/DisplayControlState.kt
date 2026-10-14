package com.gwolbisa.ledgram.ui.display

internal data class DisplayControlState(
    val isLocked: Boolean = false,
    val isOverlayVisible: Boolean = false,
    val overlayRevision: Int = 0,
) {
    val canExit: Boolean
        get() = !isLocked

    fun toggleOverlay(): DisplayControlState =
        if (isOverlayVisible) hideOverlay() else revealOverlay()

    fun revealOverlay(): DisplayControlState = copy(
        isOverlayVisible = true,
        overlayRevision = overlayRevision + 1,
    )

    fun hideOverlay(): DisplayControlState = copy(isOverlayVisible = false)

    fun lock(): DisplayControlState = copy(
        isLocked = true,
        isOverlayVisible = true,
        overlayRevision = overlayRevision + 1,
    )

    fun unlock(): DisplayControlState = copy(
        isLocked = false,
        isOverlayVisible = true,
        overlayRevision = overlayRevision + 1,
    )
}
