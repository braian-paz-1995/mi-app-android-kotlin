package com.ationet.androidterminal.core.domain.model

sealed interface ProcessState {
    data object Ok : ProcessState
    data object NOk : ProcessState
}