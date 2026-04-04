package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation

import androidx.room.Embedded
import androidx.room.Relation

data class LoyaltyAccumulationWithFusion(
    @Embedded val loyaltyAccumulation: LoyaltyAccumulationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loyaltyAccumulation_id"
    )
    val fusion: FusionLoyaltyAccumulationEntity
)