package com.ationet.androidterminal.core.domain.repository

import androidx.paging.PagingData
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptView
import kotlinx.coroutines.flow.Flow


interface ReceiptRepository {
    /**
     * Get the receipt headers of a fuel point by their indices.
     * */
    fun getReceiptHeaders(batchId: Int, controllerType: String, pumpId: Int? = null): Flow<PagingData<ReceiptView>>
    /**
     * Gets a receipt by its id
     *
     * */
    suspend fun getReceipt(id: Int): Receipt?
    /**
     * Saves a new receipt.
     *
     * @return Receipt unique id
     * */
    suspend fun saveReceipt(receipt: Receipt): Int

    suspend fun delete(id: Int): Boolean

    suspend fun getAll(): List<Receipt>
}