package com.ationet.androidterminal.standalone.batch_close.data.local

import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.repository.OperationStateRepository
import javax.inject.Inject
import javax.inject.Singleton

data class BatchCloseOperationState(
    val batchId: String = "",
    val countSales: Int = 0,
    val countVoid: Int = 0,
    val countRechargeCC: Int = 0,
    val countReverseCC: Int = 0,
    val totalSales: Double = 0.0,
    val totalVoid: Double = 0.0,
    val totalRechargeCC: Double = 0.0,
    val totalReverseCC: Double = 0.0,
    val receipt: Receipt? = null,


)

@Singleton
class BatchCloseStateRepository @Inject constructor() :
    OperationStateRepository<BatchCloseOperationState> {
    private var state : BatchCloseOperationState = BatchCloseOperationState()
    override fun clear() {
        state = BatchCloseOperationState()
    }

    override fun getState(): BatchCloseOperationState = state

    override fun updateState(block: (BatchCloseOperationState) -> BatchCloseOperationState) {
        state = block(state)
    }
}