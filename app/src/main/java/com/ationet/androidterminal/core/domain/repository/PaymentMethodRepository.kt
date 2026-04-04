package com.ationet.androidterminal.core.domain.repository

import com.ationet.androidterminal.core.domain.model.paymentMethod.PaymentMethod

interface PaymentMethodRepository {
    suspend fun create(paymentMethod: PaymentMethod): PaymentMethod
    suspend fun update(paymentMethod: PaymentMethod): Boolean
    suspend fun delete(id: List<Int>): Boolean
    suspend fun list(): List<PaymentMethod>

}