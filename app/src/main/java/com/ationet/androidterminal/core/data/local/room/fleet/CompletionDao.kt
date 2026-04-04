package com.ationet.androidterminal.core.data.local.room.fleet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.CompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.CompletionWithFusion
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.FusionCompletionEntity

@Dao
interface CompletionDao {
    @Insert
    suspend fun create(completion: CompletionEntity): Long

    @Insert
    suspend fun create(fusionCompletion: FusionCompletionEntity)

    @Transaction
    suspend fun create(completion: CompletionEntity, fusionCompletion: FusionCompletionEntity): CompletionWithFusion {
        val rowId = create(completion).toInt()

        create(fusionCompletion.copy(completionId = rowId))

        return CompletionWithFusion(
            completion = completion.copy(id = rowId),
            fusion = fusionCompletion.copy(completionId = rowId)
        )
    }

    @Query("SELECT * FROM completion WHERE id=:id")
    @Transaction
    suspend fun getFusionCompletion(id: Int): CompletionWithFusion?

    @Query("SELECT * FROM completion WHERE id=:id")
    suspend fun get(id: Int): CompletionEntity?

    @Query("SELECT * FROM completion")
    suspend fun getAll(): List<CompletionEntity>

    @Query("DELETE FROM completion WHERE id=:id")
    suspend fun delete(id: Int): Int
}