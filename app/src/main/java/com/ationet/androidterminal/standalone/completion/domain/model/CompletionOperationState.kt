package com.ationet.androidterminal.standalone.completion.domain.model

import com.ationet.androidterminal.core.domain.model.receipt.Receipt

data class CompletionOperationState(
    val receipt: Receipt?,
)
