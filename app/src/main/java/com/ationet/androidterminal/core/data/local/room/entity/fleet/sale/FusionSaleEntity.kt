package com.ationet.androidterminal.core.data.local.room.entity.fleet.sale

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_sale",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            entity = SaleEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("sale_id")]
)
data class FusionSaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_sale_id")
    val fusionSaleId: Int,
    @ColumnInfo(name = "sale_id")
    val saleId: Int,
)
