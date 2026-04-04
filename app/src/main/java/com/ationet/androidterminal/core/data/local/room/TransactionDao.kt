package com.ationet.androidterminal.core.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.ationet.androidterminal.core.data.local.room.entity.transaction.TransactionView

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transaction_view\n" +
            "WHERE  authorization_code=:authorizationCode AND\n" +
            "       batch_id = :batchId\n" +
            "ORDER BY datetime(transaction_date_time) DESC\n" +
            "LIMIT 1")
    suspend fun getTransactionByAuthorizationCode(
        authorizationCode: String,
        batchId: Int,
    ): TransactionView?
}