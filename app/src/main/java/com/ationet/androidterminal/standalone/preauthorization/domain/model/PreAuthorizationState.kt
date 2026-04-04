package com.ationet.androidterminal.standalone.preauthorization.domain.model

import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.maintenance.product.domain.model.Product

data class PreAuthorizationState(
    val identification: String = "",
    val pumpId: Int = 0,
    val product: Product? = null,
    val quantity: Double = 0.0,
    val amount: Double = 0.0,
    val displayType: DisplayType = DisplayType.QUANTITY,
    val authorizationCode: String = "",
    val localTransactionDate: String = "",
    val localTransactionTime: String = "",
    val quantityRequest: Double = 0.0,
    val quantityAuthorized: Double = 0.0,
    val amountAuthorized: Double = 0.0,
    val transactionSequenceNumber: Long = 0,
    val invoice: String = "",
)