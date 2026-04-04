package com.ationet.androidterminal.core.domain.model.receipt

import com.ationet.androidterminal.core.domain.model.batch.BatchTransaction
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatchTransaction

data class BatchCloseData(
    val sales: List<BatchTransaction> = emptyList(),
    val rechargeCC: List<BatchTransaction> = emptyList(),
    val reverseCC: List<BatchTransaction> = emptyList(),
    val voids: List<BatchTransaction> = emptyList(),
)
data class LoyaltyBatchCloseData(
    val sales: List<LoyaltyBatchTransaction> = emptyList(),
    val rechargeCC: List<LoyaltyBatchTransaction> = emptyList(),
    val reverseCC: List<LoyaltyBatchTransaction> = emptyList(),
    val voids: List<LoyaltyBatchTransaction> = emptyList(),
)

