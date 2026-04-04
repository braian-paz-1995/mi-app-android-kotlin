package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.loyalty.loyalty_points_redemption.domain.model.LoyaltyPointsRedemption

interface LoyaltyPointsRedemptionRepository {
    suspend fun createLoyaltyPointsRedemption(loyaltyPointsRedemption: LoyaltyPointsRedemption): LoyaltyPointsRedemption
    suspend fun getLoyaltyPointsRedemption(id: Int): LoyaltyPointsRedemption?
    suspend fun getAllLoyaltyPointsRedemption(): List<LoyaltyPointsRedemption>
    suspend fun deleteLoyaltyPointsRedemption(id: Int) : Boolean
}