package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PriceData(
    @SerialName("ProductUnitPrice")
    val productUnitPrice: Double?,
    @SerialName("ProductUnitPriceBase")
    val productUnitPriceBase: Double?,
    @SerialName("ProductAmount")
    val productAmount: Double?,
    @SerialName("TransactionAmount")
    val transactionAmount: Double?,
    @SerialName("Modifiers")
    val modifiers: List<PriceModifier>
)

@Serializable
data class PriceModifier(
    @SerialName("Description")
    val description: String,
    @SerialName("Value")
    val value: Double,
    @SerialName("Total")
    val total: Double,
    @SerialName("Base")
    val base: Double,
    @SerialName("Type")
    val type: Int,
    @SerialName("Class")
    val modifierClass: Int,
)