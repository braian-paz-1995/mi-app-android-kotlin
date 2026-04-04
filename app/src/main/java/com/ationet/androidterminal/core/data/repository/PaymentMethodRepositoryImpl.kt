package com.ationet.androidterminal.core.data.repository

import com.ationet.androidterminal.core.data.local.room.entity.tm.payment_method.PaymentMethodEntity
import com.ationet.androidterminal.core.data.local.room.tm.PaymentMethodDao
import com.ationet.androidterminal.core.domain.model.paymentMethod.PaymentMethod
import com.ationet.androidterminal.core.domain.repository.PaymentMethodRepository
import javax.inject.Inject

class PaymentMethodRepositoryImpl @Inject constructor(
    private val dao: PaymentMethodDao
) : PaymentMethodRepository {

    override suspend fun create(paymentMethod: PaymentMethod): PaymentMethod {
        val entity = PaymentMethodEntity(
            code = paymentMethod.code,
            name = paymentMethod.name,
            order = paymentMethod.order,
            isCC = paymentMethod.isCC,
            isLoyalty = paymentMethod.isLoyalty
        )
        val id = dao.insert(entity)
        return paymentMethod.copy(id = id.toInt())
    }

    override suspend fun update(paymentMethod: PaymentMethod): Boolean {
        val entity = PaymentMethodEntity(
            id = paymentMethod.id,
            code = paymentMethod.code,
            name = paymentMethod.name,
            order = paymentMethod.order,
            isCC = paymentMethod.isCC,
            isLoyalty = paymentMethod.isLoyalty

        )
        return dao.update(entity) > 0
    }

    override suspend fun delete(id: List<Int>): Boolean {
        return dao.delete(id) >= 0
    }

    override suspend fun list(): List<PaymentMethod> {
        return dao.getAll().map {
            PaymentMethod(
                id = it.id,
                code = it.code,
                name = it.name,
                order = it.order,
                isCC = it.isCC,
                isLoyalty = it.isLoyalty
            )
        }
    }

    suspend fun upsert(paymentMethod: PaymentMethodEntity): Int {
        return if (paymentMethod.id > 0) {
            val updatedRows = dao.update(paymentMethod)
            if (updatedRows > 0) {
                paymentMethod.id
            } else {
                dao.insert(paymentMethod.copy(id = 0)).toInt()
            }
        } else {
            dao.insert(paymentMethod.copy(id = 0)).toInt()
        }
    }


    /**
     * Inicializa los métodos de pago predeterminados solo si la tabla está vacía
     */
    suspend fun initDefaultPaymentMethods() {
        val list = dao.getAll()
        if (list.isEmpty()) {
            dao.insert(PaymentMethodEntity(code = 1, name = "Card Credit", order = 1, isCC = true))
            dao.insert(PaymentMethodEntity(code = 2, name = "Card Debit", order = 2, isCC = true))
            dao.insert(PaymentMethodEntity(code = 3, name = "Cash", order = 3, isCC = true))
            dao.insert(PaymentMethodEntity(code = 4, name = "Loyalty Card Credit", order = 4, isLoyalty = true))
            dao.insert(PaymentMethodEntity(code = 5, name = "Loyalty Card Debit", order = 5, isLoyalty = true))
            dao.insert(PaymentMethodEntity(code = 6, name = "Loyalty Cash", order = 6, isLoyalty = true))
        }
    }
}
