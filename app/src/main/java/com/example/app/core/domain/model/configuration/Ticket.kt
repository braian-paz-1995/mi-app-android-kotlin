package com.ationet.androidterminal.core.domain.model.configuration

import com.ationet.androidterminal.core.domain.model.configuration.Configuration.Companion.Defaults

data class Ticket(
    val driverIdentification: Boolean = Defaults.DEFAULT_DRIVER_IDENTIFICATION,
    val vehicleIdentification: Boolean = Defaults.DEFAULT_VEHICLE_IDENTIFICATION,
    val companyName: Boolean = Defaults.DEFAULT_COMPANY_NAME,
    val merchantId: Boolean = Defaults.DEFAULT_MERCHANT_ID,
    val primaryIdentification: Boolean = Defaults.DEFAULT_PRIMARY_IDENTIFICATION,
    val secondaryIdentification: Boolean = Defaults.DEFAULT_SECONDARY_IDENTIFICATION,
    val transactionDetails: Boolean = Defaults.DEFAULT_TRANSACTION_DETAILS,
    val title: String = Defaults.DEFAULT_TICKET_TITLE,
    val subtitle: String = Defaults.DEFAULT_TICKET_SUBTITLE,
    val footer: String = Defaults.DEFAULT_TICKET_FOOTER,
    val bottomNote: String = Defaults.DEFAULT_TICKET_BOTTOM_NOTE,
    val invoiceNumberInsteadOfAuthorizationCode: Boolean = Defaults.DEFAULT_INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE,
    val isDetailInColumn: Boolean = Defaults.DEFAULT_TRANSACTION_DETAILS_IN_COLUMNS
)