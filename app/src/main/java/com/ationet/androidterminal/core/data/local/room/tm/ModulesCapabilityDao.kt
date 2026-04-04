package com.ationet.androidterminal.core.data.local.room.tm

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.tm.modules.ModulesCapabilityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModulesCapabilityDao {

    @Query("SELECT * FROM modules_capability WHERE id = :id LIMIT 1")
    suspend fun getOnce(id: String = ModulesCapabilityEntity.SINGLETON_ID): ModulesCapabilityEntity?

    @Query("SELECT * FROM modules_capability WHERE id = :id LIMIT 1")
    fun observe(id: String = ModulesCapabilityEntity.SINGLETON_ID): Flow<ModulesCapabilityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ModulesCapabilityEntity)

    @Query("DELETE FROM modules_capability WHERE id = :id")
    suspend fun deleteSingleton(id: String = ModulesCapabilityEntity.SINGLETON_ID)
}
