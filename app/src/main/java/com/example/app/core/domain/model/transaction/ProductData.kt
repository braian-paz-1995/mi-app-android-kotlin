package com.ationet.androidterminal.core.domain.model.transaction

data class ProductData(
    val inputType: String,
    val name: String,
    val code: String,
    val unitPrice: Double,
    val quantity: Double,
    val amount: Double,
)