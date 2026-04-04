package com.ationet.androidterminal.core.data.remote.ationet.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductData(
    @SerialName("ProductCode")
    val productCode: String,
    @SerialName("ProductQuantity")
    val productQuantity: Double,
    @SerialName("ProductAmount")
    val productAmount: Double,
    @SerialName("ProductNetAmount")
    val productNetAmount: Double,
    @SerialName("ProductUnitPrice")
    val productUnitPrice: Double,
    @SerialName("ProductNetUnitPrice")
    val productNetUnitPrice: Double,
    @SerialName("UnitCode")
    val unitCode: String,
    @SerialName("SKUCategory")
    val skuCategory: String?,
)