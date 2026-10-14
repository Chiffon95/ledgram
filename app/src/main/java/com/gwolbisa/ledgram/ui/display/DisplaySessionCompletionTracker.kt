package com.gwolbisa.ledgram.ui.display

internal class DisplaySessionCompletionTracker {
    private var completed = false

    fun completeOnce(onCompleted: () -> Unit): Boolean {
        if (completed) return false
        completed = true
        onCompleted()
        return true
    }
}
