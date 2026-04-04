package com.ationet.androidterminal.hal.printer

import android.graphics.Bitmap
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.hal.printer.ImageFormat
import com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus
import com.ationet.androidterminal.core.domain.hal.printer.TextFormat
import javax.inject.Inject

class HALPrinterImpl @Inject constructor() : HALPrinter {

    override val status: PrinterStatus
        get() = PrinterStatus.Ok

    override fun write(
        text: String,
        format: TextFormat
    ): Boolean = true

    override fun drawLine(format: TextFormat): Boolean = true

    override fun drawImage(
        image: Bitmap,
        format: ImageFormat
    ): Boolean = true

    override fun drawTable(init: HALPrinter.Table.() -> Unit): Boolean = true

    override fun feedPaper(steps: Int): Boolean = true

    override suspend fun print(feedSteps: Int): PrinterStatus {
        return PrinterStatus.Ok
    }
}