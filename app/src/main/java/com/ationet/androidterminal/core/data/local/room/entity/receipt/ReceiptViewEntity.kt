package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import kotlinx.datetime.LocalDateTime

@DatabaseView(
    viewName = "receipt_view",
    value = "SELECT id,\n" +
            "       transaction_name,\n" +
            "       transaction_date_time,\n" +
            "       authorization_code,\n" +
            "       customer_vehicle_code,\n" +
            "       customer_driver_name,\n" +
            "       product_amount,\n" +
            "       product_quantity,\n" +
            "       unit_of_measure,\n" +
            "       currency_symbol,\n" +
            "       created_date_time,\n" +
            "       response_code,\n" +
            "       response_text,\n" +
            "       tsn,\n" +
            "       batch_id, \n" +
            "       pump_id, \n" +
            "       controller_owner,\n" +
            "       copy\n" +
            "FROM receipt")
data class ReceiptViewEntity(
    @ColumnInfo(name = "id") val receiptId: Int,
    @ColumnInfo(name = "transaction_name") val transactionName: String,
    @ColumnInfo(name = "transaction_date_time") val transactionDateTime: LocalDateTime,
    @ColumnInfo(name = "authorization_code") val authorizationCode: String?,
    @ColumnInfo(name = "customer_vehicle_code") val vehicle: String?,
    @ColumnInfo(name = "customer_driver_name") val driver: String?,
    @ColumnInfo(name = "product_amount") val amount: Double?,
    @ColumnInfo(name = "product_quantity") val quantity: Double?,
    @ColumnInfo(name = "unit_of_measure") val unitOfMeasure: String,
    @ColumnInfo(name = "currency_symbol") val currencySymbol: String,
    @ColumnInfo(name = "created_date_time") val createdDateTime: LocalDateTime,
    @ColumnInfo(name = "batch_id") val batchId: Int,
    @ColumnInfo(name = "response_text") val responseMessage : String?,
    @ColumnInfo(name = "response_code") val responseCode: String?,
    @ColumnInfo(name = "tsn") val tsn : String?,
    @ColumnInfo(name = "pump_id", defaultValue = 0.toString()) val pumpId : Int,
    @ColumnInfo(name = "controller_owner", defaultValue = "STAND_ALONE") val controllerOwner : String,
    @ColumnInfo(name = "copy", defaultValue = "false") val copy : Boolean
)
