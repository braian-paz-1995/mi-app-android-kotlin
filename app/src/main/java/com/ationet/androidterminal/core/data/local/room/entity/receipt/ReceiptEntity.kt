package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "receipt",
    indices = [Index(value = ["batch_id"])]
)
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("copy")
    val copy: Boolean,
    @ColumnInfo("controller_owner")
    val controllerOwner: String,
    @Embedded(prefix = "header_")
    val header: ReceiptHeaderEntity,
    @Embedded(prefix = "footer_")
    val footer: ReceiptFooterEntity,
    @Embedded(prefix = "transaction_")
    val transactionLine: ReceiptTransactionTypeEntity,
    @Embedded(prefix = "site_")
    val site: ReceiptSiteEntity,
    @Embedded
    val printConfiguration: ReceiptPrintConfigurationEntity,
    @Embedded
    val transactionData: ReceiptTransactionDataEntity,
    @ColumnInfo("created_date_time")
    val createdDateTime: LocalDateTime,
    @ColumnInfo("batch_id")
    val batchId: Int,
    @ColumnInfo("pump_id", defaultValue = 0.toString())
    val pumpId: Int
)
