package com.ationet.androidterminal.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object GlobalDebounceState {
    private val _isDebouncing = MutableStateFlow(false)
    val isDebouncing: StateFlow<Boolean> = _isDebouncing

    fun startDebounce() {
        _isDebouncing.value = true
    }

    fun stopDebounce() {
        _isDebouncing.value = false
    }
}
