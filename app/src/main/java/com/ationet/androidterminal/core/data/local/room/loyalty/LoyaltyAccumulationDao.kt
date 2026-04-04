package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation.FusionLoyaltyAccumulationEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation.LoyaltyAccumulationEntity
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_accumulation.LoyaltyAccumulationWithFusion


@Dao
interface LoyaltyAccumulationDao {
    @Insert
    suspend fun create(loyaltyAccumulation: LoyaltyAccumulationEntity): Long

    @Insert
    suspend fun create(fusionLoyaltyAccumulation: FusionLoyaltyAccumulationEntity)

    @Transaction
    suspend fun create(loyaltyAccumulation: LoyaltyAccumulationEntity, fusionLoyaltyAccumulation: FusionLoyaltyAccumulationEntity): LoyaltyAccumulationWithFusion {
        val rowId = create(loyaltyAccumulation).toInt()

        create(fusionLoyaltyAccumulation.copy(loyaltyAccumulationId = rowId))

        return LoyaltyAccumulationWithFusion(
            loyaltyAccumulation = loyaltyAccumulation.copy(id = rowId),
            fusion = fusionLoyaltyAccumulation.copy(loyaltyAccumulationId = rowId)
        )
    }

    @Query("SELECT * FROM loyaltyAccumulation WHERE id=:id")
    @Transaction
    suspend fun getFusionLoyaltyAccumulation(id: Int): LoyaltyAccumulationWithFusion?

    @Query("SELECT * FROM loyaltyAccumulation WHERE id=:id")
    suspend fun get(id: Int): LoyaltyAccumulationEntity?

    @Query("SELECT * FROM loyaltyAccumulation")
    suspend fun getAll(): List<LoyaltyAccumulationEntity>

    @Query("DELETE FROM loyaltyAccumulation WHERE id=:id")
    suspend fun delete(id: Int): Int
}