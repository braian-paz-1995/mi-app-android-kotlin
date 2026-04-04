package com.ationet.androidterminal.core.domain.model.paymentMethod

data class PaymentMethod(
    val id: Int = 0,
    val code: Int,
    val name: String,
    val order: Int,
    val isCC: Boolean = false,
    val isLoyalty: Boolean = false
)
