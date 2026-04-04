package com.ationet.androidterminal.standalone.sale.data.repository

import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.ProductData
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.SaleEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.sale.TransactionData
import com.ationet.androidterminal.core.data.local.room.fleet.SaleDao
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.repository.Sale
import com.ationet.androidterminal.core.domain.repository.SaleRepository
import javax.inject.Inject

class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao,
) : SaleRepository {
    override suspend fun create(sale: Sale): Sale {
        val saleEntity = with(sale) {
            SaleEntity(
                authorizationCode = authorizationCode,
                transactionDateTime = transactionDateTime,
                transactionSequenceNumber = transactionSequenceNumber,
                transactionData = TransactionData(
                    primaryTrack = sale.transactionData.primaryTrack,
                    product = with(sale.transactionData.product) {
                        ProductData(
                            inputType = inputType,
                            name = name,
                            code = code,
                            quantity = quantity,
                            amount = amount,
                            unitPrice = unitPrice
                        )
                    }
                ),
                batchId = batchId,
                controllerType = controllerType.toString()
            )
        }

        val rowId = saleDao.create(saleEntity)
        return sale.copy(
            id = rowId.toInt()
        )
    }

    override suspend fun getById(id: Int): Sale? {
        val databaseSale = saleDao.get(id) ?: return null
        return databaseSale.mapToDomain
    }

    override suspend fun getAll(): List<Sale> {
        return saleDao.getAll().map { it.mapToDomain }
    }

    override suspend fun delete(id: Int): Boolean {
        return saleDao.delete(id) > 0
    }

    private val SaleEntity.mapToDomain: Sale
        get() = Sale(
            authorizationCode = authorizationCode,
            transactionDateTime = transactionDateTime,
            transactionSequenceNumber = transactionSequenceNumber,
            transactionData = com.ationet.androidterminal.core.domain.repository.TransactionData(
                primaryTrack = transactionData.primaryTrack,
                product = with(transactionData.product) {
                    com.ationet.androidterminal.core.domain.repository.ProductData(
                        inputType = inputType,
                        name = name,
                        code = code,
                        quantity = quantity,
                        amount = amount,
                        unitPrice = unitPrice,
                    )
                }
            ),
            batchId = batchId,
            controllerType = Configuration.ControllerType.valueOf(controllerType)
        )
}