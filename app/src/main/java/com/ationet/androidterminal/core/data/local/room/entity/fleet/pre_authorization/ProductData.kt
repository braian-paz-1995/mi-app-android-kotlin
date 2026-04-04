package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo

data class ProductData(
    @ColumnInfo(name = "grade") val grade: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "corporate_number") val corporateNumber: String?,
    @ColumnInfo(name = "unit_price") val unitPrice: Double?,
)