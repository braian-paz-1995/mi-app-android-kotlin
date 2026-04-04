package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.loyalty.loyalty_balance_enquiry.domain.model.LoyaltyBalanceEnquiry

interface LoyaltyBalanceEnquiryRepository {
    suspend fun createLoyaltyBalanceEnquiry(loyaltyBalanceEnquiry: LoyaltyBalanceEnquiry): LoyaltyBalanceEnquiry
    suspend fun getLoyaltyBalanceEnquiry(id: Int): LoyaltyBalanceEnquiry?
    suspend fun getAllLoyaltyBalanceEnquiry(): List<LoyaltyBalanceEnquiry>
    suspend fun deleteLoyaltyBalanceEnquiry(id: Int) : Boolean
}