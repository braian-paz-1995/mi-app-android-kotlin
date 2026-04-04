package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fusion_loyaltyRewardsRedemption",
    foreignKeys = [
        ForeignKey(
            parentColumns = ["id"],
            childColumns = ["loyaltyRewardsRedemption_id"],
            entity = LoyaltyRewardsRedemptionEntity::class,
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("loyaltyRewardsRedemption_id")]
)
data class FusionLoyaltyRewardsRedemptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "fusion_loyaltyRewardsRedemption_id")
    val fusionLoyaltyRewardsRedemptionId: Int,
    @ColumnInfo(name = "loyaltyRewardsRedemption_id")
    val loyaltyRewardsRedemptionId: Int,
)

