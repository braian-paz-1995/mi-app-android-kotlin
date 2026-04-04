package com.ationet.androidterminal.core.data.local.room.loyalty

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.loyalty.loyalty_void_transaction.LoyaltyVoidTransactionEntity

@Dao
interface LoyaltyVoidDao {
    @Insert
    suspend fun insertLoyaltyVoidTransaction(loyaltyVoidTransaction: LoyaltyVoidTransactionEntity): Long

    @Query("DELETE FROM loyalty_void_transaction WHERE id = :id")
    suspend fun delete(id: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM loyalty_void_transaction WHERE old_Authorization_Code = :oldAuthorizationCode)")
    suspend fun getTransactionsByAuthorizationCode(oldAuthorizationCode: String): Boolean
}