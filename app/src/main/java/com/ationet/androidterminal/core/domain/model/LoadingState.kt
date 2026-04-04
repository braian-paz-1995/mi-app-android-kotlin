package com.ationet.androidterminal.core.domain.model

sealed interface LoadingState {
    data object Loading : LoadingState
    data object Success : LoadingState
    data object Failure : LoadingState
}