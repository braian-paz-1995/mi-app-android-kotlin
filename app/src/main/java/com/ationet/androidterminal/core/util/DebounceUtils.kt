package com.ationet.androidterminal.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun CoroutineScope.runGlobalDebounced(
    debounceTime: Long = 1000L,
    action: suspend () -> Unit
) {
    if (!GlobalDebounceState.isDebouncing.value) {
        GlobalDebounceState.startDebounce()
        launch {
            try {
                action()
            } finally {
                delay(debounceTime)
                GlobalDebounceState.stopDebounce()
            }
        }
    } else {
        launch {
            delay(debounceTime)
            GlobalDebounceState.stopDebounce()
        }
    }
}
