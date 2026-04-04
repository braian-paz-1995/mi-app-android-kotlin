package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption

import androidx.room.Embedded
import androidx.room.Relation


data class LoyaltyPointsRedemptionWithFusion(
    @Embedded val loyaltyPointsRedemption: LoyaltyPointsRedemptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loyaltyPointsRedemption_id"
    )
    val fusion: FusionLoyaltyPointsRedemptionEntity
)