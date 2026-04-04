package com.ationet.androidterminal.core.domain.repository

interface OperationStateRepository<State> {
    fun clear()
    fun getState(): State
    fun updateState(block: State.() -> State)
}