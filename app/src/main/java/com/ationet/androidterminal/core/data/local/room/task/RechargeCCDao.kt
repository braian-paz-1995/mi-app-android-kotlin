package com.ationet.androidterminal.core.data.local.room.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC.FusionRechargeCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC.RechargeCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.rechargeCC.RechargeCCWithFusion

@Dao
interface RechargeCCDao {
    @Insert
    suspend fun create(rechargeCC: RechargeCCEntity): Long

    @Insert
    suspend fun create(fusionRechargeCC: FusionRechargeCCEntity)

    @Transaction
    suspend fun create(rechargeCC: RechargeCCEntity, fusionRechargeCC: FusionRechargeCCEntity): RechargeCCWithFusion {
        val rowId = create(rechargeCC).toInt()

        create(fusionRechargeCC.copy(rechargeCCId = rowId))

        return RechargeCCWithFusion(
            rechargecc = rechargeCC.copy(id = rowId),
            fusion = fusionRechargeCC.copy(rechargeCCId = rowId)
        )
    }

    @Query("SELECT * FROM rechargeCC WHERE id=:id")
    @Transaction
    suspend fun getFusionRechargeCC(id: Int): RechargeCCWithFusion?

    @Query("SELECT * FROM rechargeCC WHERE id=:id")
    suspend fun get(id: Int): RechargeCCEntity?

    @Query("SELECT * FROM rechargeCC")
    suspend fun getAll(): List<RechargeCCEntity>

    @Query("DELETE FROM rechargeCC WHERE id=:id")
    suspend fun delete(id: Int): Int
}