package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.fusion.core.domain.use_case.TransactionState

data class PreAuthorizationFusion(
    val preAuthorization: PreAuthorization,
    val pumpId: Int,
    val saleId: Int?,
    val product: Product? = null,
    val state: TransactionState,
    val completionAmount: Double = 0.0,
    val completionQuantity: Double = 0.0
)
