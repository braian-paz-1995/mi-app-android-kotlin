package com.ationet.androidterminal.core.data.local.room.entity.transaction

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import kotlinx.datetime.LocalDateTime

@DatabaseView(
    viewName = "transaction_view",
    value = "SELECT\n" +
            "	C.id,\n" +
            "	C.authorization_code,\n" +
            "	C.transaction_sequence_number,\n" +
            "	C.transaction_date_time,\n" +
            "	C.transaction_primary_track,\n" +
            "	C.batch_id,\n" +
            "	C.controller_type,\n" +
            "	C.transaction_product_input_type,\n" +
            "	C.transaction_product_name,\n" +
            "	C.transaction_product_code,\n" +
            "	C.transaction_product_unit_price,\n" +
            "	C.transaction_product_quantity,\n" +
            "	C.transaction_product_amount,\n" +
            "	'LoyaltyAccumulation' AS type\n" +
            "FROM\n" +
            "	loyaltyAccumulation AS C\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "	C.id,\n" +
            "	C.authorization_code,\n" +
            "	C.transaction_sequence_number,\n" +
            "	C.transaction_date_time,\n" +
            "	C.transaction_primary_track,\n" +
            "	C.batch_id,\n" +
            "	C.controller_type,\n" +
            "	C.transaction_product_input_type,\n" +
            "	C.transaction_product_name,\n" +
            "	C.transaction_product_code,\n" +
            "	C.transaction_product_unit_price,\n" +
            "	C.transaction_product_quantity,\n" +
            "	C.transaction_product_amount,\n" +
            "	'Completion' AS type\n" +
            "FROM\n" +
            "	completion AS C\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "	S.id,\n" +
            "	S.authorization_code,\n" +
            "	S.transaction_sequence_number,\n" +
            "	S.transaction_date_time,\n" +
            "	S.transaction_primary_track,\n" +
            "	S.batch_id,\n" +
            "	S.controller_type,\n" +
            "	S.transaction_product_input_type,\n" +
            "	S.transaction_product_name,\n" +
            "	S.transaction_product_code,\n" +
            "	S.transaction_product_unit_price,\n" +
            "	S.transaction_product_quantity,\n" +
            "	S.transaction_product_amount,\n" +
            "	'Sale' AS type\n" +
            "FROM\n" +
            "	sale AS S\n" +
            "UNION ALL\n" +
            "SELECT\n" +
            "	P.id,\n" +
            "	P.authorization_code,\n" +
            "	P.transaction_sequence_number AS transaction_sequence_number,\n" +
            "	P.local_datetime AS transaction_date_time,\n" +
            "	P.primary_track AS transaction_primary_track,\n" +
            "	P.batch_id AS batch_id,\n" +
            "	P.controller_type AS controller_type,\n" +
            "	NULL AS transaction_product_input_type,\n" +
            "	NULL AS transaction_product_name,\n" +
            "	NULL AS transaction_product_code,\n" +
            "	NULL AS transaction_product_unit_price,\n" +
            "	NULL AS transaction_product_quantity,\n" +
            "	NULL AS transaction_product_amount,\n" +
            "	'PreAuthorization' AS type\n" +
            "FROM\n" +
            "	pre_authorization AS P"
)
data class TransactionView(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "authorization_code") val authorizationCode: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "transaction_sequence_number") val transactionSequenceNumber: Long,
    @ColumnInfo(name = "transaction_date_time") val dateTime: LocalDateTime,
    @Embedded(prefix = "transaction_") val transactionData: TransactionData,
    @ColumnInfo(name = "batch_id") val batchId: Int,
    @ColumnInfo(name = "controller_type") val controllerType: String
)