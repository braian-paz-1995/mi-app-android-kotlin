package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption.FusionLoyaltyPointsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption.LoyaltyPointsRedemptionEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_points_redemption.LoyaltyPointsRedemptionWithFusion

@Dao
interface LoyaltyPointsRedemptionDao {
    @Insert
    suspend fun create(LoyaltyPointsRedemption: LoyaltyPointsRedemptionEntity): Long

    @Insert
    suspend fun create(fusionLoyaltyPointsRedemption: FusionLoyaltyPointsRedemptionEntity)

    @Transaction
    suspend fun create(LoyaltyPointsRedemption: LoyaltyPointsRedemptionEntity, fusionLoyaltyPointsRedemption: FusionLoyaltyPointsRedemptionEntity): LoyaltyPointsRedemptionWithFusion {
        val rowId = create(LoyaltyPointsRedemption).toInt()

        create(fusionLoyaltyPointsRedemption.copy(loyaltyPointsRedemptionId = rowId))

        return LoyaltyPointsRedemptionWithFusion(
            loyaltyPointsRedemption = LoyaltyPointsRedemption.copy(id = rowId),
            fusion = fusionLoyaltyPointsRedemption.copy(loyaltyPointsRedemptionId = rowId)
        )
    }

    @Query("SELECT * FROM LoyaltyPointsRedemption WHERE id=:id")
    @Transaction
    suspend fun getFusionLoyaltyPointsRedemption(id: Int): LoyaltyPointsRedemptionWithFusion?

    @Query("SELECT * FROM LoyaltyPointsRedemption WHERE id=:id")
    suspend fun get(id: Int): LoyaltyPointsRedemptionEntity?

    @Query("SELECT * FROM LoyaltyPointsRedemption")
    suspend fun getAll(): List<LoyaltyPointsRedemptionEntity>

    @Query("DELETE FROM LoyaltyPointsRedemption WHERE id=:id")
    suspend fun delete(id: Int): Int
}