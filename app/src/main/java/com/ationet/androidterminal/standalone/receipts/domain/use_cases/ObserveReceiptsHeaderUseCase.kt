package com.ationet.androidterminal.standalone.receipts.domain.use_cases

import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveReceiptsHeaderUseCase @Inject constructor(
    private val receiptRepository: ReceiptRepository
) {
    suspend operator fun invoke() {

    }
}