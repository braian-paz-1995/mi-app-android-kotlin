package com.ationet.androidterminal.core.util

import com.ationet.androidterminal.core.data.remote.ationet.ReceiptDataKeys
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptData
import kotlinx.serialization.json.Json

private val json: Json = Json {
    isLenient = true
    coerceInputValues = true
}

fun receiptData(receiptData: String?): ReceiptData {
    if (receiptData.isNullOrBlank()) {
        return ReceiptData(
            customerPan = null,
            companyName = null,
            customerPlate = null,
            customerDriverId = null,
            customerDriverName = null,
            customerVehicleCode = null,
            customerName = null,
            availableBalance = null,
            companyCode = null,
            contractCode = null,
            operationType = null
        )
    }

    val receiptDataMap = json.decodeFromString<Map<String, String?>>(receiptData)
    return ReceiptData(
        customerPan = receiptDataMap[ReceiptDataKeys.CustomerPANKey],
        companyName = receiptDataMap[ReceiptDataKeys.CompanyNameKey],
        customerPlate = receiptDataMap[ReceiptDataKeys.CustomerPlateKey],
        customerDriverId = receiptDataMap[ReceiptDataKeys.CustomerDriverIdKey],
        customerDriverName = receiptDataMap[ReceiptDataKeys.CustomerDriverNameKey],
        customerVehicleCode = receiptDataMap[ReceiptDataKeys.CustomerVehicleCodeKey],
        customerName = receiptDataMap[ReceiptDataKeys.CustomerNameKey],
        availableBalance = receiptDataMap[ReceiptDataKeys.AvailableBalanceKey],
        companyCode = receiptDataMap[ReceiptDataKeys.CompanyCodeKey],
        contractCode = receiptDataMap[ReceiptDataKeys.ContractCodeKey],
        operationType = receiptDataMap[ReceiptDataKeys.OperationTypeKey]
    )

}