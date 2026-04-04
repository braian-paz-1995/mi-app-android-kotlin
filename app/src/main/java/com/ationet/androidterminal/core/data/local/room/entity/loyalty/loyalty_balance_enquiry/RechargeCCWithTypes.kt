package com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry

import androidx.room.Embedded
import androidx.room.Relation

data class LoyaltyBalanceEnquiryWithFusion(
    @Embedded val loyaltyBalanceEnquiry: LoyaltyBalanceEnquiryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "loyaltyBalanceEnquiry_id"
    )
    val fusion: FusionLoyaltyBalanceEnquiryEntity
)