package com.ationet.androidterminal.hal.printer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.printer.*
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.verifone.payment_sdk.PaymentSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.delay

class HALPrinterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val paymentSdk: PaymentSdk,
) : HALPrinter {

    private val sdiManager = paymentSdk.sdiManager
    private val printItems = mutableListOf<PrintItem>()

    // List fo words in order to manage spacial br/ inside the ticket

    // Add both a br before and after the text
    private val lineBreakKeywordsUpAndDown = listOf(
        "TOTAL",
        "Product", // Both for EN and ES (Product-o)
        "TSN",
        "Terminal ID",
        "ID de terminal"
    )

    // Add a br/ only before the text
    private val lineBreakKeywordsOnlyUp = listOf(
        "Authorization code",
        "Código de autorización"
    )

    // Add a br/ only after the text
    private val lineBreakKeywordsOnlyDown = listOf(
        "Invoice number",
        "Número de factura",
        "Quantity",
        "Cantidad",
        "Total cancelado",
        "Total cancelled"
    )


    override val status: PrinterStatus
        get() = PrinterStatus.Ok

    override fun write(text: String, format: TextFormat): Boolean {
        printItems.add(PrintItem(text = text, format = format, type = PrintItemType.Text))

        if (format.linefeed == true) {
            //
            printItems.add(PrintItem(text = "", type = PrintItemType.Feed))
        }
        return true
    }

    override fun drawLine(format: TextFormat): Boolean {
        // Adds a separating line
        printItems.add(PrintItem(text = "-".repeat(38), format = format, type = PrintItemType.Line))

        if (format.linefeed == true) {
            printItems.add(PrintItem(text = "", type = PrintItemType.Feed))
        }
        return true
    }

    override fun drawImage(image: Bitmap, format: ImageFormat): Boolean {
        Log.w(TAG, "T650P printer: drawImage is not supported via DirectPrintService.")
        return false
    }

    override fun drawTable(init: HALPrinter.Table.() -> Unit): Boolean {
        val table = HALPrinter.Table().apply(init)
        val row = table.rows.firstOrNull() ?: return false

        val tableHtml = StringBuilder()

        // If the transaction detalils are set to be shown as columns, it creates the corresponding table
        val useCompactTable = !row.cells.any { it.text.contains("|") }

        // Checks if the selected langauge is spanish
        val isSpanish = printItems.any {
            it.text.contains("Código de autorización", ignoreCase = false) ||
                    it.text.contains("ID de terminal", ignoreCase = false) ||
                    it.text.contains("Número de factura", ignoreCase = false) ||
                    it.text.contains("Producto", ignoreCase = false)
        }
        if (useCompactTable) {
            val quantityLabel = if (isSpanish) "CANT" else "QTY"
            val amountLabel = if (isSpanish) "MONTO" else "AMOUNT"

            tableHtml.append("""
                <table class="product-table">
                    <thead>
                        <tr>
                            <th>PROD</th>
                            <th>$quantityLabel</th>
                            <th>PPU</th>
                            <th>$amountLabel</th>
                        </tr>
                    </thead>
                    <tbody>
            """.trimIndent())

            tableHtml.append("<tr>")
            row.cells.forEach { cell ->
                val value = cell.text.trim()
                tableHtml.append("<td>$value</td>")
            }
            tableHtml.append("</tr>")
            tableHtml.append("</tbody></table>")

            printItems.add(PrintItem(text = tableHtml.toString(), type = PrintItemType.Table))
            printItems.add(PrintItem(text = "", type = PrintItemType.Feed))

            return true
        } else {
            return true
        }
    }

    override fun feedPaper(steps: Int): Boolean {
        repeat(steps) {
            printItems.add(PrintItem(text = "", type = PrintItemType.Feed))
        }
        return true
    }

    override suspend fun print(feedSteps: Int): PrinterStatus {

        // Adds a small timeout to allow the "Printing Copy" screen to load and be seen.
        delay(timeMillis = 500)
        return suspendCancellableCoroutine { continuation ->
            try {
                feedPaper(feedSteps)
                val htmlContent = buildHtml()
                 Log.e(TAG, htmlContent)

                // Uses the verifone SDK to finally print the HTML ticket.
                val result = sdiManager.printer.printHTML(htmlContent, false)

                if (result.name == "OK") {
                    Log.d(TAG, "HTML print completed successfully.")
                    continuation.resume(PrinterStatus.Ok)
                } else {
                    Log.e(TAG, "HTML print failed with result: ${result.name}")
                    continuation.resume(PrinterStatus.Error)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Print failed due to exception", e)
                continuation.resume(PrinterStatus.Error)
            } finally {
                printItems.clear()
            }
        }
    }

    private fun buildHtml(): String {
        val contentBuilder = StringBuilder()
        var i = 0

        // Later used to avoid extra line feeds in case of error message
        var counterOfErrorMessages = 0
        val endLines: List<String> = listOf(
            "Commerce ticket",
            "Client copy",
            "Ticket comercio",
            "Copia cliente"
        )

        while (i < printItems.size) {
            val current = printItems[i]

            Log.e(TAG, "Info step : ${current}")

            // There are certain error messages that are stored in two different items. For example, Mapeo de combustible requerido
            // comes as (Mapeo de combustible), (requerido), and both have the css style of title. Therefore, when it reaches the text validation
            // to add a br/ if the css class is "title", it ends up leaving a line feed (salto de linea) between the Mapeo de combustible, and requerido.
            // Before the error, comes the text of TSN, and after the error comes ComerceTicket or Client's Copy. So it checks for the amount of
            // texts to print in between and if the amount is >= 8 (including "" line feeds) it avoids adding the br/ if the css style is "title".

            var c = i

            if (current.text.contains("TSN:", ignoreCase = false)) {
                try {
                    while (c < printItems.size && (printItems[c].text) !in endLines) {

                        // As long as the messages are not IN the endLines list, it keeps adding
                        c++
                        counterOfErrorMessages++
                    }
                } catch (e: IndexOutOfBoundsException) {
                    Log.e(TAG, "Counter of error messages exceeded the max index of printItems")
                }
            }

            // If the current text is in the endLines list, it resets the errorMessage counter to allow the corresponding line feeds with the
            // footer and the bottom note
            if (endLines.any { current.text.contains(it, ignoreCase = false) }) {
                counterOfErrorMessages = 0
                contentBuilder.append("<br>")
            }

            // if true, it avoids adding the br/ in between the error messages
            val avoidLineFeed = counterOfErrorMessages >= 8






            // Adds a br accordingly based on the text, if it belongs to any of the lists.
            if (lineBreakKeywordsOnlyUp.any { current.text.contains(it, ignoreCase = false) }) {
                contentBuilder.append("<br>")
            }
            if (lineBreakKeywordsUpAndDown.any { current.text.contains(it, ignoreCase = false) }) {
                contentBuilder.append("<br>")
            }

            // Aligns the DATE-PREVIEW-TIME table to be on the same line
            if (
                i + 2 < printItems.size &&
                current.format?.alignment == Alignment.Left &&
                printItems[i + 1].format?.alignment == Alignment.Center &&
                printItems[i + 2].format?.alignment == Alignment.Right
            ) {
                val left = current.text
                val center = printItems[i + 1].text
                val right = printItems[i + 2].text

                contentBuilder.append(
                    """
            <br/>
            <table class='date-table'>
                <tr>
                    <td class='date'>$left</td>
                    <td class='preview'>$center</td>
                    <td class='time'>$right</td>
                </tr>
            </table>
            <br/>
            """.trimIndent()
                )

                i += 3
                continue
            }

            // If the transaction info is NOT detailed in columns, it creates a table aligning left-right the corresponding values
            if (
                i + 1 < printItems.size &&
                current.format?.alignment == Alignment.Left &&
                printItems[i + 1].format?.alignment == Alignment.Right
            ) {
                val leftText = current.text.trim()
                val rightText = printItems[i + 1].text.trim()

                contentBuilder.append("""
                <table class='two-column-table'>
                    <tr>
                        <td class='left-cell'>$leftText</td>
                        <td class='right-cell'>$rightText</td>
                    </tr>
                </table>
            """.trimIndent())

                i += 2
                continue
            }

            // Adds the content to the HTML

            when (current.type) {
                PrintItemType.Feed -> {
                    // goes on
                }

                PrintItemType.Line -> {
                    contentBuilder.append("<div class='line'>${current.text}</div><br>")
                }

                PrintItemType.Table -> {
                    contentBuilder.append("<div class='table'>${current.text}</div>")
                }

                PrintItemType.Text -> {
                    val cssClass = classForFormat(current.format)
                    contentBuilder.append("<p class='$cssClass'>${current.text}</p>")


                    // Special case that can't be handled neither via css style, as the T650p printer
                    // Does not recognise margin-bottom: property, and the title can't be included inside
//                    // The lineBreak lists, as it has an unknown value.
                    if (cssClass == "title" && !avoidLineFeed) {
                        contentBuilder.append("<br>")
                    }
                }
            }

            // Adds a br accordingly based on the text, if it belongs to any of the lists.
            if (lineBreakKeywordsOnlyDown.any { current.text.contains(it, ignoreCase = false) }) {
                contentBuilder.append("<br>")
            }
            if (lineBreakKeywordsUpAndDown.any { current.text.contains(it, ignoreCase = false) }) {
                contentBuilder.append("<br>")
            }

            i++
        }

        // Creates the styles for the HTML
        val styles = """
            body { font-family: monospace; font-size: 16px; white-space: pre-wrap; }

            .title { font-size: 25px; font-weight: bold; text-align: center; margin: 15px 0; margin-bottom: 100px;}
            .subtitle { font-size: 18px; font-weight: bold; text-align: center; margin: 4px 0; }
            .bold-left { font-size: 20px; font-weight: bold; text-align: left; margin: 4px 0; }
            .bold-right { font-weight: bold; text-align: right; margin: 4px 0; }
           .normal-left {font-size:17px;text-align: left; margin: 2px 0; font-weight: bold;}
            .normal-right {font-size:18px; text-align: right; margin: 2px 0;font-weight: bold; }

            .line { border-top: 1px dashed #000; margin: 6px 0; }
            .table { font-family: monospace; margin: 6px 0; width: 100%; }

            .product-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 16px;
            }

            .product-table th,
            .product-table td {
                text-align: center;
                padding: 5px 11px;
            }

            .product-table th { font-weight: bold; }
            .product-table td { font-weight: bold; }

            .date-table {
                table-layout: auto;
                width: 100%;
                font-family: monospace;
                font-size: 14px;
            }

            .date-table td {
                padding: 4px;
                vertical-align: middle;
            }

            .date-table .date {
                text-align: left;
            }

            .date-table .preview {
                text-align: center;
            }

            .date-table .time {
                text-align: right;
            }

            .two-column-table {
                width: 100%;
                font-family: monospace;
                font-size: 14px;
            }

            .two-column-table td {
                padding: 2px 4px;
                vertical-align: top;
            }

            .two-column-table .left-cell {
                text-align: left;
                width: 50%;
            }

            .two-column-table .right-cell {
                text-align: right;
                width: 50%;
            }
        """.trimIndent()

        // Creates the HTML structure
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>$styles</style>
            </head>
            <body>
                $contentBuilder
                <br><br><br><br><br><br><br><br>
            </body>
            </html>
        """.trimIndent()
    }


    private fun classForFormat(format: TextFormat?): String {
        if (format == null) return "normal-left"

        // According to the TextFormat recieved from main, it assigns a certain css class
        return when {
            format.textWeight == TextWeight.Bold &&
                    format.textSize == TextSize.Large &&
                    format.alignment == Alignment.Center -> "title"

            format.textSize != TextSize.Small &&
                    format.alignment == Alignment.Center -> "subtitle"

            format.textWeight == TextWeight.Bold &&
                    format.alignment == Alignment.Left -> "bold-left"

            format.textWeight == TextWeight.Bold &&
                    format.alignment == Alignment.Right -> "bold-right"

            format.alignment == Alignment.Left -> "normal-left"

            format.alignment == Alignment.Right -> "normal-right"

            else -> "normal-left"
        }
    }

    //
    private data class PrintItem(
        val text: String,
        val format: TextFormat? = null,
        val type: PrintItemType = PrintItemType.Text
    )

    private enum class PrintItemType {
        Text,
        Line,
        Table,
        Feed
    }

    companion object {
        private const val TAG = "HALPrinterT650P"
    }
}
