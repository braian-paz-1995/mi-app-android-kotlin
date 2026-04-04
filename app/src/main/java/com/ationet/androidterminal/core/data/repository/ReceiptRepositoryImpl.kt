package com.ationet.androidterminal.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.ationet.androidterminal.core.data.local.room.ReceiptDao
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptDataEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptFooterEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptHeaderEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptPrintConfigurationEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptProductEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptProductModifierEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptSiteEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptTransactionDataEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptTransactionTypeEntity
import com.ationet.androidterminal.core.data.local.room.entity.receipt.ReceiptWithModifiers
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptFooter
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptHeader
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierClass
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptModifierType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptPrintConfiguration
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProduct
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptProductInputType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptSite
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionData
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionType
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptView
import com.ationet.androidterminal.core.domain.model.receipt.TransactionModifier
import com.ationet.androidterminal.core.domain.repository.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepositoryImpl @Inject constructor(
    private val receiptDao: ReceiptDao,
) : ReceiptRepository {
    override fun getReceiptHeaders(batchId: Int, controllerOwner: String, pumpId: Int?): Flow<PagingData<ReceiptView>> {

        return Pager(config = PagingConfig(pageSize = 20)) {
            ReceiptViewPager(receiptDao, batchId, controllerOwner, pumpId)
        }.flow.map { pagingData ->
            pagingData.filter {
                val transactionType =
                    runCatching { ReceiptTransactionTypeName.valueOf(it.transactionName) }.getOrNull()
                transactionType == ReceiptTransactionTypeName.Completion || transactionType == ReceiptTransactionTypeName.Sale
            }
                .map {
                    ReceiptView(
                        receiptId = it.receiptId,
                        transactionName = ReceiptTransactionTypeName.valueOf(it.transactionName),
                        transactionDateTime = it.transactionDateTime,
                        authorizationCode = it.authorizationCode?.let { it } ?: "",
                        vehicle = it.vehicle,
                        driver = it.driver,
                        amount = it.amount,
                        quantity = it.quantity,
                        unitOfMeasure = it.unitOfMeasure,
                        currencySymbol = it.currencySymbol,
                        responseMessage = it.responseMessage,
                        responseCode = it.responseCode,
                        copy = it.copy
                    )
                }
        }
    }

    override suspend fun getReceipt(id: Int): Receipt? {
        val dbReceipt = receiptDao.getReceipt(id)
        return dbReceipt?.toModel()
    }

    override suspend fun saveReceipt(receipt: Receipt): Int {
        val receiptEntity = receipt.toEntity()
        val createdReceipt =
            receiptDao.createReceipt(receiptEntity.receipt, receiptEntity.modifiers)
        return createdReceipt.receipt.id
    }

    override suspend fun delete(id: Int): Boolean {
        return receiptDao.delete(id) > 0
    }

    override suspend fun getAll(): List<Receipt> {
        return receiptDao.getAll().map { it.toModel() }
    }

    private fun Receipt.toEntity(): ReceiptWithModifiers {
        return ReceiptWithModifiers(
            receipt = ReceiptEntity(
                id = id,
                copy = copy,
                controllerOwner = controllerOwner.name,
                transactionLine = ReceiptTransactionTypeEntity(
                    dateTime = transactionLine.dateTime,
                    name = transactionLine.name.name
                ),
                header = ReceiptHeaderEntity(
                    title = header.title,
                    subtitle = header.subtitle,
                ),
                footer = ReceiptFooterEntity(
                    footer = footer.footer,
                    bottomNote = footer.bottomNote
                ),
                site = ReceiptSiteEntity(
                    name = site.name,
                    code = site.code,
                    address = site.address,
                    cuit = site.cuit,
                ),
                printConfiguration = ReceiptPrintConfigurationEntity(
                    printDriver = printConfiguration.printDriver,
                    printVehicle = printConfiguration.printVehicle,
                    printCompanyName = printConfiguration.printCompanyName,
                    printInvoiceNumber = printConfiguration.printInvoiceNumber,
                    printPrimaryTrack = printConfiguration.printPrimaryTrack,
                    printSecondaryTrack = printConfiguration.printSecondaryTrack,
                    printTransactionDetails = printConfiguration.printTransactionDetails,
                    printProductInColumns = printConfiguration.printProductInColumns
                ),
                transactionData = ReceiptTransactionDataEntity(
                    responseCode = transactionData.responseCode,
                    responseText = transactionData.responseText,
                    primaryTrack = transactionData.primaryTrack,
                    secondaryTrack = transactionData.secondaryTrack,
                    authorizationCode = transactionData.authorizationCode,
                    terminalId = transactionData.terminalId,
                    transactionSequenceNumber = transactionData.transactionSequenceNumber,
                    invoice = transactionData.invoice,
                    product = ReceiptProductEntity(
                        name = transactionData.product.name,
                        code = transactionData.product.code,
                        inputType = transactionData.product.inputType.name,
                        unitPrice = transactionData.product.unitPrice,
                        amount = transactionData.product.amount,
                        quantity = transactionData.product.quantity,
                    ),
                    receiptData = ReceiptDataEntity(
                        customerPan = transactionData.receiptData.customerPan,
                        customerPlate = transactionData.receiptData.customerPlate,
                        customerDriverName = transactionData.receiptData.customerDriverName,
                        customerDriverId = transactionData.receiptData.customerDriverId,
                        customerVehicleCode = transactionData.receiptData.customerVehicleCode,
                        companyName = transactionData.receiptData.companyName
                    ),
                    unitOfMeasure = transactionData.unitOfMeasure,
                    currencySymbol = transactionData.currencySymbol,
                    transactionAmount = transactionData.transactionAmount ?: 0.0
                ),
                createdDateTime = createdDateTime,
                batchId = batchId,
                pumpId = pumpId
                    ?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
                    ?.toIntOrNull()
                    ?: 0

            ),
            modifiers = transactionData.product.modifiers.map { it.toEntity() },
        )
    }

    private fun TransactionModifier.toEntity(): ReceiptProductModifierEntity {
        return ReceiptProductModifierEntity(
            id = id,
            receiptId = receiptId,
            type = type.name,
            modifierClass = modifierClass.name,
            value = value,
            total = total,
            base = base
        )
    }

    private fun ReceiptWithModifiers.toModel(): Receipt {
        return with(receipt) {
            Receipt(
                id = id,
                copy = copy,
                controllerOwner = Configuration.ControllerType.valueOf(controllerOwner),
                transactionLine = ReceiptTransactionType(
                    dateTime = transactionLine.dateTime,
                    name = ReceiptTransactionTypeName.valueOf(transactionLine.name)
                ),
                header = ReceiptHeader(
                    title = header.title,
                    subtitle = header.subtitle,
                ),
                footer = ReceiptFooter(
                    footer = footer.footer,
                    bottomNote = footer.bottomNote
                ),
                site = ReceiptSite(
                    name = site.name,
                    code = site.code,
                    address = site.address,
                    cuit = site.cuit,
                ),
                printConfiguration = ReceiptPrintConfiguration(
                    printDriver = printConfiguration.printDriver,
                    printVehicle = printConfiguration.printVehicle,
                    printCompanyName = printConfiguration.printCompanyName,
                    printInvoiceNumber = printConfiguration.printInvoiceNumber,
                    printPrimaryTrack = printConfiguration.printPrimaryTrack,
                    printSecondaryTrack = printConfiguration.printSecondaryTrack,
                    printTransactionDetails = printConfiguration.printTransactionDetails,
                    printProductInColumns = printConfiguration.printProductInColumns
                ),
                transactionData = ReceiptTransactionData(
                    responseCode = transactionData.responseCode,
                    responseText = transactionData.responseText,
                    primaryTrack = transactionData.primaryTrack,
                    secondaryTrack = transactionData.secondaryTrack,
                    authorizationCode = transactionData.authorizationCode,
                    terminalId = transactionData.terminalId,
                    transactionSequenceNumber = transactionData.transactionSequenceNumber,
                    invoice = transactionData.invoice,
                    product = ReceiptProduct(
                        name = transactionData.product.name,
                        code = transactionData.product.code,
                        inputType = ReceiptProductInputType.valueOf(transactionData.product.inputType),
                        unitPrice = transactionData.product.unitPrice,
                        amount = transactionData.product.amount,
                        quantity = transactionData.product.quantity,
                        modifiers = this@toModel.modifiers.map { it.toModel() },
                    ),
                    receiptData = ReceiptData(
                        customerPan = transactionData.receiptData.customerPan,
                        customerPlate = transactionData.receiptData.customerPlate,
                        customerDriverName = transactionData.receiptData.customerDriverName,
                        customerDriverId = transactionData.receiptData.customerDriverId,
                        customerVehicleCode = transactionData.receiptData.customerVehicleCode,
                        companyName = transactionData.receiptData.companyName
                    ),
                    unitOfMeasure = transactionData.unitOfMeasure,
                    currencySymbol = transactionData.currencySymbol,
                    transactionAmount = transactionData.transactionAmount
                ),
                createdDateTime = createdDateTime,
                batchId = batchId,
                pumpId = pumpId.toString()
            )
        }
    }

    private fun ReceiptProductModifierEntity.toModel(): TransactionModifier {
        return TransactionModifier(
            id = id,
            type = ReceiptModifierType.valueOf(type),
            modifierClass = ReceiptModifierClass.valueOf(modifierClass),
            value = value,
            receiptId = receiptId,
            total = total,
            base = base
        )
    }
}
