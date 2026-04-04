package com.ationet.androidterminal.core.data.local.room.fleet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.fleet.void_transaction.VoidTransactionEntity

@Dao
interface VoidDao {
    @Insert
    suspend fun insertVoidTransaction(voidTransaction: VoidTransactionEntity): Long

    @Query("DELETE FROM void_transaction WHERE id = :id")
    suspend fun delete(id: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM void_transaction WHERE Authorization_Code = :authorizationCode)")
    suspend fun getTransactionsByAuthorizationCode(authorizationCode: String): Boolean
}