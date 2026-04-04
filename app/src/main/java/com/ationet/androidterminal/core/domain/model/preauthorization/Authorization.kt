package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.data.local.room.entity.fleet.pre_authorization.AuthorizationData
import kotlinx.datetime.LocalDateTime

data class Authorization(
    val authorizationCode: String,
    val transactionSequenceNumber: Long,
    val invoice: String,
    val localDateTime: LocalDateTime,
    val authorizedAmount: Double?,
    val authorizedQuantity: Double?,
    val authorizedPrice: Double?
) {
    fun toEntity(): AuthorizationData = AuthorizationData(
        authorizationCode = this.authorizationCode,
        transactionSequenceNumber = this.transactionSequenceNumber,
        invoice = this.invoice,
        localDateTime = this.localDateTime,
        amount = this.authorizedAmount,
        quantity = this.authorizedQuantity,
        unitPrice = this.authorizedPrice
    )
}