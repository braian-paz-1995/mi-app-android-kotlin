package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption.FusionLoyaltyRewardsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption.LoyaltyRewardsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_rewards_redemption.LoyaltyRewardsRedemptionWithFusion

@Dao
interface LoyaltyRewardsRedemptionDao {
    @Insert
    suspend fun create(loyaltyRewardsRedemption: LoyaltyRewardsRedemptionEntity): Long

    @Insert
    suspend fun create(fusionLoyaltyRewardsRedemption: FusionLoyaltyRewardsRedemptionEntity)

    @Transaction
    suspend fun create(loyaltyRewardsRedemption: LoyaltyRewardsRedemptionEntity, fusionLoyaltyRewardsRedemption: FusionLoyaltyRewardsRedemptionEntity): LoyaltyRewardsRedemptionWithFusion {
        val rowId = create(loyaltyRewardsRedemption).toInt()

        create(fusionLoyaltyRewardsRedemption.copy(loyaltyRewardsRedemptionId = rowId))

        return LoyaltyRewardsRedemptionWithFusion(
            loyaltyRewardsRedemption = loyaltyRewardsRedemption.copy(id = rowId),
            fusion = fusionLoyaltyRewardsRedemption.copy(loyaltyRewardsRedemptionId = rowId)
        )
    }

    @Query("SELECT * FROM loyaltyRewardsRedemption WHERE id=:id")
    @Transaction
    suspend fun getFusionLoyaltyRewardsRedemption(id: Int): LoyaltyRewardsRedemptionWithFusion?

    @Query("SELECT * FROM loyaltyRewardsRedemption WHERE id=:id")
    suspend fun get(id: Int): LoyaltyRewardsRedemptionEntity?

    @Query("SELECT * FROM loyaltyRewardsRedemption")
    suspend fun getAll(): List<LoyaltyRewardsRedemptionEntity>

    @Query("DELETE FROM loyaltyRewardsRedemption WHERE id=:id")
    suspend fun delete(id: Int): Int
}