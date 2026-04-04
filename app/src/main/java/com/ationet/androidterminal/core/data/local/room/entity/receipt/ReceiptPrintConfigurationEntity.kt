package com.ationet.androidterminal.core.data.local.room.entity.receipt

import androidx.room.ColumnInfo

data class ReceiptPrintConfigurationEntity(
    @ColumnInfo("print_driver")
    val printDriver: Boolean,
    @ColumnInfo("print_vehicle")
    val printVehicle: Boolean,
    @ColumnInfo("print_company_name")
    val printCompanyName: Boolean,
    @ColumnInfo("print_primary_track")
    val printPrimaryTrack: Boolean,
    @ColumnInfo("print_secondary_track")
    val printSecondaryTrack: Boolean,
    @ColumnInfo("print_transaction_details")
    val printTransactionDetails: Boolean,
    @ColumnInfo("print_product_in_columns")
    val printProductInColumns: Boolean,
    @ColumnInfo("print_invoice_number")
    val printInvoiceNumber: Boolean
)