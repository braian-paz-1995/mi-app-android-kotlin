package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.loyalty.loyalty_rewards_redemption.domain.model.LoyaltyRewardsRedemption


interface LoyaltyRewardsRedemptionRepository {
    suspend fun createLoyaltyRewardsRedemption(loyaltyRewardsRedemption: LoyaltyRewardsRedemption): LoyaltyRewardsRedemption
    suspend fun getLoyaltyRewardsRedemption(id: Int): LoyaltyRewardsRedemption?
    suspend fun getAllLoyaltyRewardsRedemption(): List<LoyaltyRewardsRedemption>
    suspend fun deleteLoyaltyRewardsRedemption(id: Int) : Boolean
}