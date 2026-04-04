package com.ationet.androidterminal.core.data.local.room.entity.tm.payment_method

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paymentMethod")
data class PaymentMethodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "code") val code: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "order_value") val order: Int,
    @ColumnInfo(name = "is_cc", defaultValue = "0") val isCC: Boolean = false,
    @ColumnInfo(name = "is_loyalty", defaultValue = "0") val isLoyalty: Boolean = false
)