package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption

import androidx.room.Embedded
import androidx.room.Relation

data class LoyaltyRewardsRedemptionWithFusion(
    @Embedded val loyaltyRewardsRedemption: LoyaltyRewardsRedemptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loyaltyRewardsRedemption_id"
    )
    val fusion: FusionLoyaltyRewardsRedemptionEntity
)