package com.ationet.androidterminal.core.data.local.room.task

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC.FusionReverseCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC.ReverseCCEntity
import com.ationet.androidterminal.core.data.local.room.entity.task.reverseCC.ReverseCCWithFusion

@Dao
interface ReverseCCDao {
    @Insert
    suspend fun create(reverseCC: ReverseCCEntity): Long

    @Insert
    suspend fun create(fusionReverseCC: FusionReverseCCEntity)

    @Transaction
    suspend fun create(reverseCC: ReverseCCEntity, fusionReverseCC: FusionReverseCCEntity): ReverseCCWithFusion {
        val rowId = create(reverseCC).toInt()

        create(fusionReverseCC.copy(reverseCCId = rowId))

        return ReverseCCWithFusion(
            reversecc = reverseCC.copy(id = rowId),
            fusion = fusionReverseCC.copy(reverseCCId = rowId)
        )
    }

    @Query("SELECT * FROM rechargeCC WHERE id=:id")
    @Transaction
    suspend fun getFusionReverseCC(id: Int): ReverseCCWithFusion?

    @Query("SELECT * FROM reverseCC WHERE id=:id")
    suspend fun get(id: Int): ReverseCCEntity?

    @Query("SELECT * FROM reverseCC")
    suspend fun getAll(): List<ReverseCCEntity>

    @Query("DELETE FROM reverseCC WHERE id=:id")
    suspend fun delete(id: Int): Int
}