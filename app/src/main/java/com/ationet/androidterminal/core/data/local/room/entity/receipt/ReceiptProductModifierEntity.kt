package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipt_product_modifiers",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receipt_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["receipt_id"]
        )
    ]
)
data class ReceiptProductModifierEntity(
    @ColumnInfo("type")
    val type: String,
    @ColumnInfo("class")
    val modifierClass: String,
    @ColumnInfo("value")
    val value: Double,
    @ColumnInfo("base", defaultValue = "0.0")
    val base: Double,
    @ColumnInfo("total", defaultValue = "0.0")
    val total: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("receipt_id")
    val receiptId: Int = 0,
)