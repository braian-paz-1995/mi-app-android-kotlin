package com.ationet.androidterminal.core.domain.model

data class Prompt(
    val id: Int = 0,
    val key: String,
    val value: String? = null,
    val state: PromptState,
    val type: PromptType,
) {
    enum class PromptState {
        Pending,
        Completed,
    }

    enum class PromptType {
        Identifier,
        Pin,
        Alphanumeric,
        Numeric,
    }
}