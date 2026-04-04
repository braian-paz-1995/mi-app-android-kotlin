package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_LoyaltyAccumulation",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["loyaltyAccumulation_id"],
            entity = LoyaltyAccumulationEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("loyaltyAccumulation_id")]
)
data class FusionLoyaltyAccumulationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_LoyaltyAccumulation_id")
    val fusionLoyaltyAccumulationId: Int,
    @ColumnInfo(name = "loyaltyAccumulation_id")
    val loyaltyAccumulationId: Int,
)

