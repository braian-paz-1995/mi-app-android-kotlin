package com.ationet.androidterminal.core.domain.util

fun generateInvoice(terminalId: String, invoice: String): String {
    val terminalNumber = getTerminalNumber(terminalId)

    val invoiceNumber = invoice.padStart(8, '0')

    return  "%s-%s".format(terminalNumber, invoiceNumber)
}

fun getTerminalNumber(terminalId: String): String {
    return if(terminalId.length > 3)
        terminalId.drop(3)
    else
        terminalId
}