package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.OriginalData
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class OriginalData(
    val transactionSequenceNumber: Long,
    val authorizationCode: String,
    val transactionCode: String,
    val localTransactionDate: LocalDate? = null,
    val localTransactionTime: LocalTime? = null,
){
    fun toEntity(): OriginalData = OriginalData(
        transactionSequenceNumber = this.transactionSequenceNumber,
        authorizationCode = this.authorizationCode,
        transactionCode = this.transactionCode,
        localTransactionDate = this.localTransactionDate,
        localTransactionTime = this.localTransactionTime
    )
}