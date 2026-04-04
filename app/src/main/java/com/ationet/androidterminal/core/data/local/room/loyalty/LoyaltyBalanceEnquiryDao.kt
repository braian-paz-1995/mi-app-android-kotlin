package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry.FusionLoyaltyBalanceEnquiryEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry.LoyaltyBalanceEnquiryEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_balance_enquiry.LoyaltyBalanceEnquiryWithFusion

@Dao
interface LoyaltyBalanceEnquiryDao {
    @Insert
    suspend fun create(loyaltyBalanceEnquiry: LoyaltyBalanceEnquiryEntity): Long

    @Insert
    suspend fun create(fusionLoyaltyBalanceEnquiry: FusionLoyaltyBalanceEnquiryEntity)

    @Transaction
    suspend fun create(loyaltyBalanceEnquiry: LoyaltyBalanceEnquiryEntity, fusionLoyaltyBalanceEnquiry: FusionLoyaltyBalanceEnquiryEntity): LoyaltyBalanceEnquiryWithFusion {
        val rowId = create(loyaltyBalanceEnquiry).toInt()

        create(fusionLoyaltyBalanceEnquiry.copy(loyaltyBalanceEnquiryId = rowId))

        return LoyaltyBalanceEnquiryWithFusion(
            loyaltyBalanceEnquiry = loyaltyBalanceEnquiry.copy(id = rowId),
            fusion = fusionLoyaltyBalanceEnquiry.copy(loyaltyBalanceEnquiryId = rowId)
        )
    }

    @Query("SELECT * FROM loyaltyBalanceEnquiry WHERE id=:id")
    @Transaction
    suspend fun getFusionLoyaltyBalanceEnquiry(id: Int): LoyaltyBalanceEnquiryWithFusion?

    @Query("SELECT * FROM loyaltyBalanceEnquiry WHERE id=:id")
    suspend fun get(id: Int): LoyaltyBalanceEnquiryEntity?

    @Query("SELECT * FROM loyaltyBalanceEnquiry")
    suspend fun getAll(): List<LoyaltyBalanceEnquiryEntity>

    @Query("DELETE FROM loyaltyBalanceEnquiry WHERE id=:id")
    suspend fun delete(id: Int): Int
}