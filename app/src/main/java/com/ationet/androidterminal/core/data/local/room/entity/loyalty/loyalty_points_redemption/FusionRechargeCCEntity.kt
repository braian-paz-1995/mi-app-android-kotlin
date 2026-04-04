package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_loyaltyPointsRedemption",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["loyaltyPointsRedemption_id"],
            entity = LoyaltyPointsRedemptionEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("loyaltyPointsRedemption_id")]
)
data class FusionLoyaltyPointsRedemptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_loyaltyPointsRedemption_id")
    val fusionLoyaltyPointsRedemptionId: Int,
    @ColumnInfo(name = "loyaltyPointsRedemption_id")
    val loyaltyPointsRedemptionId: Int,
)

