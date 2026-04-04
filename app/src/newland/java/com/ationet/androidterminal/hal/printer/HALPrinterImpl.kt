package com.ationet.androidterminal.hal.printer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.ationet.androidterminal.core.domain.hal.printer.Alignment
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.hal.printer.ImageFormat
import com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus
import com.ationet.androidterminal.core.domain.hal.printer.TextFormat
import com.ationet.androidterminal.core.domain.hal.printer.TextSize
import com.ationet.androidterminal.core.domain.hal.printer.TextWeight
import com.newland.sdk.module.printer.ErrorCode
import com.newland.sdk.module.printer.PrintListener
import com.newland.sdk.module.printer.PrinterModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume
import com.newland.sdk.module.printer.PrinterStatus as SDKPrinterStatus
import androidx.core.graphics.scale
import kotlin.math.ceil
import kotlin.math.roundToInt

class HALPrinterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val printerModule: PrinterModule
) : HALPrinter {

    private val scriptData: StringBuilder = StringBuilder()

    private var bitmapIndex: Int = 0
    private val bitmapMap: MutableMap<String, Bitmap> = mutableMapOf()
    private var lineOpen: Boolean = false

    init {
        initializePrinter(context)
        initializeScriptData()
    }

    override val status: PrinterStatus
        get() = printerModule.status.toHALPrinterStatus()


    override fun write(
        text: String,
        format: TextFormat
    ): Boolean {
        Log.d(TAG, "Newland printer: Writing '$text' with format '$format'")

        setFormat(format)

        val repl = text.replace('\n', '\r')
        val printableText = if (format.linefeed != true && repl.trim() == "") {
            "\r"
        } else {
            repl
        }

        if(printableText == "\r") {
            feedLine(size = BIG_LINE_FEED)
            return true
        }

        if (format.underline == true) {
            writeUnderline(printableText, format.alignment, format.linefeed == true)
        } else {
            writeText(printableText, format.alignment, format.linefeed == true)
        }

        lineOpen = format.linefeed != true

        return true
    }

    override fun drawLine(format: TextFormat): Boolean {
        Log.d(TAG, "Newland printer: printing line with format '$format'")

        if(lineOpen) {
            lineOpen = false

            /* Force to close the line by writing a empty space. */
            writeText(text = " ", Alignment.Left, lineFeed = true)
        }

        setFormat(format)

        writeLine()

        return true
    }

    override fun drawImage(
        image: Bitmap,
        format: ImageFormat
    ): Boolean {
        Log.d(TAG, "Newland printer: drawing image with format '$format'")

        val desiredWidth = minOf(format.width ?: image.width, PAPER_WIDTH)
        val desiredHeight = if (desiredWidth == image.width) {
            image.height
        } else {
            val currentRatio = (image.width * 1f) / (image.height * 1f)

            desiredWidth / currentRatio
        }.toInt()

        writeBitmap(
            bitmap = image,
            alignment = format.alignment,
            width = desiredWidth,
            height = desiredHeight
        )

        return true
    }

    // region Table
    override fun drawTable(init: HALPrinter.Table.() -> Unit): Boolean {
        val table = HALPrinter.Table()
        /* initializes table with what was declared */
        table.init()

        /* No headers? Well, nothing to draw! */
        if(table.header.isEmpty) {
            Log.d(TAG, "Table has no header")
            return true
        }

        val measuredTable = MeasuredTable(table, TABLE_LINE_MAX_CHARS)

        layoutTable(measuredTable)

        return true
    }

    private fun layoutTable(
        table: MeasuredTable
    ) {
        for (row in table.rows) {
            layoutRow(row)
        }
    }

    private fun layoutRow(
        row: MeasuredTable.MeasuredRow
    ) {
        val stringBuilder = StringBuilder()

        with(stringBuilder) {
            for (cell in row.cells) {
                val cellText = when (cell.alignment) {
                    Alignment.Left -> {
                        val padAmount = cell.width - cell.textWidth
                        cell.text.padStart(padAmount, ' ')
                    }

                    Alignment.Center -> {
                        centerText(cell.text, cell.width)
                    }

                    Alignment.Right -> {
                        val padAmount = cell.width - cell.textWidth
                        cell.text.padEnd(padAmount, ' ')
                    }
                }

                append(cellText)
            }
        }

        val line = if(stringBuilder.isNotEmpty()) {
            stringBuilder.toString()
        } else {
            ""
        }

        Log.d(TAG, "Measured row: $row")
        Log.d(TAG, "Printing line: '$line'")

        setFont(TABLE_FONT_NAME)

        /* Set font size */
        setTextSize(TextSize.Small)

        /* Set grayscale */
        setGrayscale(TextWeight.Normal)

        writeText(line, Alignment.Left, lineFeed = true)
    }

    private fun centerText(input: String, maxLength: Int) : String {
        val trimmedInput = input.substring(0 until minOf(input.length, maxLength))
        val freeLength = maxLength - trimmedInput.length

        /* Calculate pad start by taking the difference and ceiling */
        val padStartLength = ceil(freeLength / 2.0).roundToInt()
        val padEndLength = (freeLength - padStartLength).coerceAtLeast(0)
        return " ".repeat(padStartLength) + trimmedInput + " ".repeat(padEndLength)
    }
    
    // endregion Table
    override suspend fun print(feedSteps: Int): PrinterStatus {
        val status = printerModule.status
        if (status != SDKPrinterStatus.NORMAL) {
            Log.w(TAG, "Printer unavailable: $status")
            initializeScriptData()
            return status.toHALPrinterStatus()
        }

        val totalFeedSteps = maxOf(3 + feedSteps, 0)
        if (totalFeedSteps > 0) {
            feedPaper(totalFeedSteps)
        }

        Log.d(TAG, "Newland printer: printing with feed steps: $totalFeedSteps.")

        return suspendCancellableCoroutine { continuation ->
            val listener = object : PrintListener {
                override fun onSuccess() {
                    initializeScriptData()
                    continuation.resume(PrinterStatus.Ok)
                }

                override fun onError(p0: ErrorCode?, p1: String?) {
                    initializeScriptData()
                    continuation.resume(PrinterStatus.Error)
                }
            }

            printerModule.print(scriptData.toString(), bitmapMap, listener)

            continuation.invokeOnCancellation {
                /**
                 * Unregister this printing listener callback
                 * */
                printerModule.cancelStatusListener()
            }
        }
    }

    override fun feedPaper(steps: Int): Boolean {
        Log.d(TAG, "Newland printer: paper feed with steps amount: $steps")
        if(lineOpen) {
            lineOpen = false

            writeText(text = " ", Alignment.Left, lineFeed = true)
        }

        for (step in 0 until steps) {
            feedLine(size = DEFAULT_LINE_FEED)
        }

        return true
    }

    // region Printer helpers
    private fun setFormat(format: TextFormat) {
        setTextSize(format.textSize)
        val fontName = when (format.textWeight) {
            TextWeight.Thin -> THIN_FONT_NAME
            TextWeight.Normal -> NORMAL_FONT_NAME
            TextWeight.Bold -> BOLD_FONT_NAME
            TextWeight.ExtraBold -> BLACK_FONT_NAME
            null -> NORMAL_FONT_NAME
        }

        setFont(fontName)

        setGrayscale(format.textWeight)
    }

    private fun SDKPrinterStatus.toHALPrinterStatus(): PrinterStatus {
        return when (this) {
            SDKPrinterStatus.NORMAL -> PrinterStatus.Ok
            SDKPrinterStatus.OUTOF_PAPER -> PrinterStatus.OutOfPaper
            SDKPrinterStatus.OVER_HEAT -> PrinterStatus.Overheat
            SDKPrinterStatus.LOW_VOLTAGE -> PrinterStatus.UnderVoltage
            SDKPrinterStatus.BUSY -> PrinterStatus.Busy
            SDKPrinterStatus.DESTROYED -> PrinterStatus.DriverError
            SDKPrinterStatus.PPSERR -> PrinterStatus.DriverError
            SDKPrinterStatus.CUTTER_ERROR -> PrinterStatus.Error
        }
    }
    // endregion Printer helpers

    // region Printer initialization
    /**
     * Execute first run initialization
     * */
    private fun initializePrinter(context: Context) {
        val outputDirectory = File(context.filesDir, "fonts/")
        if(outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }

        copyFontToAssets(context, THIN_FONT_NAME, outputDirectory)
        copyFontToAssets(context, NORMAL_FONT_NAME, outputDirectory)
        copyFontToAssets(context, BOLD_FONT_NAME, outputDirectory)
        copyFontToAssets(context, BLACK_FONT_NAME, outputDirectory)

        copyFontToAssets(context, TABLE_FONT_NAME, outputDirectory)
    }

    private fun copyFontToAssets(context: Context, fontName: String, outputDirectory: File) {
        if(!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }
        val outputFile = File(outputDirectory, fontName)

        try {
            context.assets.open("fonts/$fontName").use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Newland printer: font '$fontName' copy failed", e)
            return
        }
    }
    // endregion

    // region Script handling
    private fun initializeScriptData() {
        scriptData.clear()

        for (entry in bitmapMap) {
            entry.value.recycle()
        }
        bitmapMap.clear()
        bitmapIndex = 0
    }

    private fun setFont(fontName: String) {
        val fontPath = printerModule.setFont(context, fontName)
        if (fontPath.isNullOrEmpty()) {
            return
        }

        scriptData.appendLine("!font $fontPath")
    }

    private fun setTextSize(textSize: TextSize?) {
        when (textSize) {
            TextSize.Small -> {
                scriptData.append("!NLFONT ")
                scriptData.append("$SmallChineseFontSize ")
                scriptData.append("$SmallFontSize ")
                scriptData.append("$FontScaleOrdinary ")
                scriptData.appendLine(SpaceUnscaled)
            }

            TextSize.Normal -> {
                scriptData.append("!NLFONT ")
                scriptData.append("$NormalChineseFontSize ")
                scriptData.append("$NormalFontSize ")
                scriptData.append("$FontScaleOrdinary ")
                scriptData.appendLine(SpaceUnscaled)
            }

            TextSize.Large -> {
                scriptData.append("!NLFONT ")
                scriptData.append("$LargeChineseFontSize ")
                scriptData.append("$LargeFontSize ")
                scriptData.append("$FontScaleOrdinary ")
                scriptData.appendLine(SpaceUnscaled)
            }

            TextSize.ExtraLarge -> {
                scriptData.append("!NLFONT ")
                scriptData.append("$LargeChineseFontSize ")
                scriptData.append("$LargeFontSize ")
                scriptData.append("$FontDoubleScaleMagnify ")
                scriptData.appendLine(SpaceUnscaled)
            }

            null -> {
                scriptData.append("!NLFONT ")
                scriptData.append("$NormalChineseFontSize ")
                scriptData.append("$NormalFontSize ")
                scriptData.append("$FontScaleOrdinary ")
                scriptData.appendLine(SpaceUnscaled)
            }
        }
    }

    private fun setGrayscale(textWeight: TextWeight?) {
        val gray = when (textWeight) {
            TextWeight.Thin -> "4"
            TextWeight.Normal -> "5"
            TextWeight.Bold -> "7"
            TextWeight.ExtraBold -> "10"
            else -> "5"
        }

        scriptData.appendLine("!gray $gray")
    }

    private fun feedLine(size: Int) {
        scriptData.appendLine("*feedline p:$size")
    }

    private fun writeUnderline(
        text: String,
        alignment: Alignment?,
        lineFeed: Boolean
    ) {
        if(lineFeed) {
            scriptData.append("*underline ")
        } else {
            scriptData.append("*UNDERLINE ")
        }

        val textAlignment = getAlignment(alignment)
        scriptData.append(textAlignment)
        scriptData.append(" ")


        /* Ensure script text has already a single space, so the printer don't get confused. */
        val scriptText = text.ifEmpty {
            " "
        }
        scriptData.appendLine(scriptText)
    }

    private fun writeText(
        text: String,
        alignment: Alignment?,
        lineFeed: Boolean
    ) {
        if(lineFeed) {
            scriptData.append("*text ")
        } else {
            scriptData.append("*TEXT ")
        }

        val textAlignment = getAlignment(alignment)
        scriptData.append(textAlignment)
        scriptData.append(" ")


        /* Ensure script text has already a single space, so the printer don't get confused. */
        val scriptText = text.ifEmpty {
            " "
        }
        scriptData.appendLine(scriptText)
    }

    private fun getAlignment(alignment: Alignment?) = when (alignment) {
        Alignment.Left -> "l"
        Alignment.Center -> "c"
        Alignment.Right -> "r"
        else -> "l" // Assume left
    }

    private fun writeLine() {
        scriptData.appendLine("*line")
    }

    private fun writeBitmap(
        bitmap: Bitmap,
        alignment: Alignment?,
        width: Int,
        height: Int
    ) {
        val image = if (width == bitmap.width) {
            /* Resize not required */
            Bitmap.createBitmap(bitmap)
        } else {
            bitmap.scale(width, height)
        }

        val key = bitmapIndex.toString()
        bitmapIndex++

        bitmapMap[key] = image

        scriptData.appendLine("*image $alignment $width*$height path:$key")
    }
    // endregion

    companion object {
        private const val TAG: String = "Printer"

        private const val PAPER_WIDTH: Int = 384

        private const val THIN_FONT_NAME = "roboto.ttf"
        private const val NORMAL_FONT_NAME = "roboto_medium.ttf"
        private const val BOLD_FONT_NAME = "roboto_bold.ttf"
        private const val BLACK_FONT_NAME = "roboto_black.ttf"

        private const val TABLE_FONT_NAME = "roboto_mono_medium.ttf"

        private const val TABLE_LINE_MAX_CHARS = 45

        private const val DEFAULT_LINE_FEED: Int = 24
        private const val BIG_LINE_FEED: Int = 32


        /**
         * PRN_ZM_FONT_8x16
         * */
        private const val SmallFontSize = "1"

        /**
         * PRN_HZ_FONT_16x16
         * */
        private const val SmallChineseFontSize = "6"

        /**
         * FONT_12x16A
         * */
        private const val NormalFontSize = "9"
        /**
         * PRN_HZ_FONT_24x24
         * */
        private const val NormalChineseFontSize = "1"

        /**
         * PRN_ZM_FONT_16x32A
         * */
        private const val LargeFontSize = "13"

        /**
         * PRN_HZ_FONT_32x32
         * */
        private const val LargeChineseFontSize = "3"

        /**
         * Unscaled
         * */
        private const val FontScaleOrdinary = "3"

        /**
         * Double magnify
         * */
        private const val FontDoubleScaleMagnify = "0"

        /**
         * Don't scale space
         * */
        private const val SpaceUnscaled = "0"
    }
}