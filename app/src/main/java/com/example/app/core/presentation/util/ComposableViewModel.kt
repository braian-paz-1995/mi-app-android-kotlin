package com.ationet.androidterminal.core.presentation.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class ComposableViewModel<State, UiAction>(private val initialState: State) : ViewModel() {
    protected var _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state get() = _state.asStateFlow()

    abstract fun onAction(action: UiAction)
}