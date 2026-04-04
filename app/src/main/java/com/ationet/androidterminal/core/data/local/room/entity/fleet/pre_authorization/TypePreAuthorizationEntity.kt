package com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_pre_authorization"
)
data class FusionPreAuthorizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "pump_id")
    val pumpId: Int,
    @ColumnInfo("sale_id")
    val saleId: Int?,
    @ColumnInfo(name = "state", defaultValue = "PRESET_SENT")
    val state: String,
    @ColumnInfo(name = "pre_authorization_id")
    val preAuthorizationId: Int,
    @ColumnInfo(name = "amount", defaultValue = "0.0")
    val amount: Double,
    @ColumnInfo(name = "quantity", defaultValue = "0.0")
    val quantity: Double,
    @Embedded(prefix = "product_") val product: ProductData
)

@Entity(
    tableName = "standalone_pre_authorization",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["pre_authorization_id"],
            entity = PreAuthorizationEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(
            value = ["pre_authorization_id"]
        )
    ]
)
data class StandAlonePreAuthorizationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "product_id")
    val productId: Int,
    @ColumnInfo(name = "product_code")
    val productCode: String,
    @ColumnInfo("product_name")
    val productName: String,
    @ColumnInfo("product_unit_price")
    val productUnitPrice: Double,
    @ColumnInfo(name = "pre_authorization_id")
    val preAuthorizationId: Int,
    @ColumnInfo(name = "input_type")
    val inputType: String,
)