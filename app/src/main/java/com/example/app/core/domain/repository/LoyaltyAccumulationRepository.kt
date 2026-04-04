package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.loyalty.loyalty_accumulation.domain.model.LoyaltyAccumulation

interface LoyaltyAccumulationRepository {
    suspend fun createLoyaltyAccumulation(loyaltyAccumulation: LoyaltyAccumulation): LoyaltyAccumulation
    suspend fun getLoyaltyAccumulation(id: Int): LoyaltyAccumulation?
    suspend fun getAllLoyaltyAccumulation(): List<LoyaltyAccumulation>
    suspend fun deleteLoyaltyAccumulation(id: Int) : Boolean
}