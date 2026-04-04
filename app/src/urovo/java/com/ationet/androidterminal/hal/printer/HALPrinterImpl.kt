package com.ationet.androidterminal.hal.printer

import android.content.Context
import android.device.PrinterManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import com.ationet.androidterminal.core.di.IoDispatcher
import com.ationet.androidterminal.core.domain.hal.printer.Alignment
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.core.domain.hal.printer.ImageFormat
import com.ationet.androidterminal.core.domain.hal.printer.PrinterStatus
import com.ationet.androidterminal.core.domain.hal.printer.TextFormat
import com.ationet.androidterminal.core.domain.hal.printer.TextSize
import com.ationet.androidterminal.core.domain.hal.printer.TextWeight
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap
import kotlin.math.ceil


class HALPrinterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val printer: PrinterManager
) : HALPrinter {
    /**
     * Total print height
     * */
    private var height: Int = 0
    private var currentLineHeight = 0

    init {
        initializePrinter(context)

        printer.setGrayLevel(DEF_PRINTER_HUE_VALUE)
        printer.setSpeedLevel(DEF_PRINTER_SPEED_VALUE)
        printer.setupPage(PAPER_WIDTH, INFINITE_SIZE)   //Set paper size
    }

    override val status: PrinterStatus
        get() = printer.status.toHALPrinterStatus()

    override fun write(text: String, format: TextFormat): Boolean {
        Log.d(
            TAG,
            "Writing '$text' with format '$format'. Line height: $currentLineHeight - Height: $height"
        )

        if(text.isEmpty() && format.linefeed == true) {
            /*
            * Reset current line height, and add that to the general height .- starting
            * a new line.
            * */
            endCurrentLine()
            return true
        }

        var style : Int = NO_STYLE
        if (format.underline == true) {
            style = style or STYLE_UNDERLINE
        }

        if (format.textWeight == TextWeight.Bold || format.textWeight == TextWeight.ExtraBold) {
            style = style or STYLE_BOLD
        }

        /* Update font size */
        val fontSize = when (format.textSize) {
            TextSize.Small -> FONT_SIZE_SMALL
            TextSize.Normal -> FONT_SIZE_NORMAL
            TextSize.Large -> FONT_SIZE_LARGE
            TextSize.ExtraLarge -> FONT_SIZE_EXTRA_LARGE
            null -> DEFAULT_FONT_SIZE
        }

        /* Update font name for weight change */
        val fontName = when (format.textWeight) {
            TextWeight.Thin -> THIN_FONT_NAME
            TextWeight.Normal -> NORMAL_FONT_NAME
            TextWeight.Bold -> BOLD_FONT_NAME
            TextWeight.ExtraBold -> BLACK_FONT_NAME
            null -> DEFAULT_FONT
        }

        /* Draw text at the specified alignment offset */
        val fontPath = getFontPath(context, fontName)
        val offset = calculateAlignmentOffset(format.alignment, text, fontSize, fontPath)
        val drawResultHeight = printer.drawTextEx(
            text,
            offset,
            height,
            INFINITE_SIZE,
            INFINITE_SIZE,
            fontPath,
            fontSize,
            NO_ROTATION,
            style,
            NO_WORD_WRAP
        )

        Log.d(TAG, "Drawn height: $drawResultHeight")

        if (drawResultHeight == -1) {
            return false
        }

        /* Update current line height if the text draw height result
        *  is greater than current line height */
        currentLineHeight = maxOf(currentLineHeight, drawResultHeight)
        if (format.linefeed == true) {
            /*
            * Reset current line height, and add that to the general height .- starting
            * a new line.
            * */
            endCurrentLine()
        }

        return true
    }

    private fun calculateAlignmentOffset(
        alignment: Alignment?,
        text: String,
        fontSize: Int,
        fontPath: String
    ): Int {
        // No alignment required
        if (alignment == null || alignment == Alignment.Left) {
            return 0
        }

        val paint = Paint().apply {
            textSize = fontSize.toFloat()
            typeface = Typeface.createFromFile(fontPath)
        }

        val textWidth = ceil(paint.measureText(text)).roundToInt()

        val offset = maxOf(PAPER_WIDTH - textWidth, 0)
        if (alignment == Alignment.Right) {
            return offset
        }

        return offset / 2
    }

    override fun drawLine(format: TextFormat): Boolean {
        Log.d(
            TAG,
            "Printing with format '$format'. Line height: $currentLineHeight - Height: $height"
        )

        /* If there is a line being written, 'end' it */
        if (currentLineHeight > 0) {
            endCurrentLine()
        }

        // TODO dotted line
        val drawHeight = printer.drawLine(ORIGIN, height, PAPER_WIDTH, height, LINE_WIDTH)
        if (drawHeight == -1) {
            return false
        }

        Log.d(TAG, "Drawn height: $drawHeight")

        height += drawHeight
        return true
    }

    override fun drawImage(image: Bitmap, format: ImageFormat): Boolean {
        Log.d(
            TAG,
            "Drawing image with format '$format'. Line height: $currentLineHeight - Height: $height"
        )

        /* If there is a line being written, 'end' it */
        if (currentLineHeight > 0) {
            endCurrentLine()
        }

        /* Coalesce print width between the requested size, the image size and the paper size
        *  If format size is not specified, coalesce between image width and paper width
        * */
        val desiredWidth = minOf(format.width ?: image.width, PAPER_WIDTH)
        /*
        * Compute width keeping aspect ratio
        * */
        val desiredHeight = when {
            /* Requested a specific height and width wasn't changed */
            format.height != null && format.width == desiredWidth -> format.height
            /* Image width hasn't changed, keep height */
            image.width == desiredWidth -> image.height
            else -> {
                val currentRatio = image.width * 1f / image.height
                (desiredWidth / currentRatio).roundToInt()
            }
        }

        /* Convert bitmap to grayscale so we ensure printer prints what we want */
        val bitmap = getGrayScaleBitmap(image, desiredWidth, desiredHeight)

        /* Coalesce between free horizontal space and zero */
        val availableHorizontalSpace = maxOf(PAPER_WIDTH - desiredWidth, 0)
        val offset = when (format.alignment) {
            /* No offset, print from left */
            Alignment.Left -> 0
            /* Print from half of the available space */
            Alignment.Center -> availableHorizontalSpace / 2
            /* Print after the available space */
            Alignment.Right -> availableHorizontalSpace
        }

        val drawResultHeight = printer.drawBitmap(bitmap, offset, height)
        if (drawResultHeight == -1) {
            return false
        }

        Log.d(TAG, "Drawn height: ${bitmap.height}")


        /* Weirdly in this case draw result height didn't have the height,
        *  so we resort to the bitmap printed */
        height += bitmap.height
        return true
    }

    // region Table
    override fun drawTable(init: HALPrinter.Table.() -> Unit): Boolean {
        /* If there is a line being written, 'end' it */
        if (currentLineHeight > 0) {
            endCurrentLine()
        }

        val table = HALPrinter.Table()

        /* initializes table with what was declared */
        table.init()

        /* No headers? Well, nothing to draw! */
        if(table.header.isEmpty) {
            Log.d(TAG, "Table has no header")
            return true
        }

        val measuredTable = MeasuredTable(
            table = table,
            paperWidth = PAPER_WIDTH,
            measureCell = this::measureCell
        )

        layoutTable(measuredTable)

        return true
    }

    private fun measureCell(
        cell: HALPrinter.Cell
    ): MeasuredTable.MeasuredCell {
        val fontPath = getFontPath(context, DEFAULT_FONT)

        /* This is the real measure, considering the entire text */
        val preferredSize = measureText(
            text = cell.text,
            fontSize = DEFAULT_FONT_SIZE,
            fontPath = fontPath
        )

        return MeasuredTable.MeasuredCell(
            text = cell.text,
            textSize = preferredSize,
            width = preferredSize.width,
            height = preferredSize.height,
            fontPath = fontPath,
            alignment = cell.alignment,
            fontSize = DEFAULT_FONT_SIZE
        )
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
        var offset = 0
        for (cell in row.cells) {

            val alignmentOffset = when (cell.alignment) {
                Alignment.Left -> 0
                Alignment.Center -> {
                    ceil(minOf(cell.width - cell.textSize.width) / 2.0).roundToInt()
                }
                Alignment.Right -> cell.width - cell.textSize.width
            }

            layoutCell(
                text = cell.text,
                x = offset + alignmentOffset,
                y = height,
                height = row.height,
                width = cell.width,
                fontPath = cell.fontPath,
                fontSize = cell.fontSize
            )

            offset += cell.width
        }

        currentLineHeight = row.height
        endCurrentLine()
    }

    private fun layoutCell(
        text: String,
        x: Int,
        y: Int,
        height: Int,
        width: Int,
        fontPath: String,
        fontSize: Int
    ): Boolean {
        Log.d(TAG, "Writing cell '$text' at ($x, $y) (size: $width W x $height H)")
        val drawnHeight = printer.drawTextEx(
            text,
            x,
            y,
            width,
            height,
            fontPath,
            fontSize,
            NO_ROTATION,
            NO_STYLE,
            NO_WORD_WRAP
        )

        return drawnHeight != -1
    }
    // endregion Table

    private fun endCurrentLine() {
        height += currentLineHeight
        currentLineHeight = 0
    }

    override fun feedPaper(steps: Int): Boolean {
        if(currentLineHeight > 0) {
            endCurrentLine()
        }

        Log.d(TAG, "Paper feed with steps amount: $steps. Height: $height")

        val text = "\n".repeat(steps)
        val feedFontSize = 8
        val drawResultHeight = printer.drawTextEx(
            text,
            ORIGIN,
            height,
            PAPER_WIDTH,
            INFINITE_SIZE,
            getFontPath(context, DEFAULT_FONT),
            feedFontSize,
            NO_ROTATION,
            NO_STYLE,
            NO_WORD_WRAP
        )

        if (drawResultHeight == -1) {
            return false
        }

        Log.d(TAG, "Drawn height: $drawResultHeight")

        height += drawResultHeight

        return true
    }

    private fun getGrayScaleBitmap(original: Bitmap, width: Int, height: Int): Bitmap {
        /* Scale down */
        val bitmap = if (width == original.width) {
            original
        } else {
            original.scale(width, height)
        }

        /* Create */
        val bmpGrayscale = createBitmap(width, height)
        bmpGrayscale.eraseColor(Color.WHITE)

        val threshold = 200
        val alphaThreshold = 55
        val chunkSize = 100
        for (x in 0 until bitmap.width step chunkSize) {
            for (y in 0 until bitmap.height step chunkSize) {
                val w = minOf(chunkSize, bitmap.width - x)
                val h = minOf(chunkSize, bitmap.height - y)
                val pixels = IntArray(w * h)

                bitmap.getPixels(pixels, 0, w, x, y, w, h)

                for (i in pixels.indices) {
                    val pixel = pixels[i]
                    val redValue = Color.red(pixel)
                    val blueValue = Color.blue(pixel)
                    val greenValue = Color.green(pixel)
                    val alphaValue = Color.alpha(pixel)
                    val grayScale = (redValue + greenValue + blueValue) / 3
                    pixels[i] = when {
                        alphaValue < alphaThreshold -> Color.WHITE
                        grayScale < threshold -> Color.BLACK
                        else -> Color.WHITE
                    }
                }

                bmpGrayscale.setPixels(pixels, 0, w, x, y, w, h)
            }
        }

        return bmpGrayscale
    }

    override suspend fun print(feedSteps: Int): PrinterStatus {
        if (printer.status != PRINTER_OK) {
            clearPrinter()
            return printer.status.toHALPrinterStatus()
        }

        /* Give a minimum space in the ticket */
        feedPaper(5)

        /* Print extra paper feed */
        if (feedSteps > 0) {
            feedPaper(feedSteps)
        }

        Log.d(TAG, "Printing with feed steps: $feedSteps. Line height: $currentLineHeight - Height: $height")

        // Wait for result in IO dispatcher
        val printResult = withContext(dispatcher) {
            printAndWaitFinish()
        }

        if (printResult != PrinterStatus.Ok) {
            return printResult
        }

        return PrinterStatus.Ok
    }

    private suspend fun printAndWaitFinish(): PrinterStatus {
        val printResult = printer.printPage(NO_ROTATION)
        if (printResult != PRINTER_OK) {
            clearPrinter()
            return printResult.toHALPrinterStatus()
        }

        var printerStatus = printer.status.toHALPrinterStatus()

        while (printerStatus != PrinterStatus.Ok) {
            if (printerStatus == PrinterStatus.Busy) {
                delay(50.milliseconds)
                printerStatus = printer.status.toHALPrinterStatus()
                continue
            }

            /* Another status, treat it as error */
            break
        }

        clearPrinter()
        return printerStatus
    }

    private fun clearPrinter() {
        /* Reset heights and clear printer */
        height = 0
        currentLineHeight = 0
        printer.clearPage()
        printer.setupPage(PAPER_WIDTH, INFINITE_SIZE)
    }

    private fun Int.toHALPrinterStatus(): PrinterStatus {
        return when (this) {
            PRINTER_OK -> PrinterStatus.Ok
            PRINTER_OUT_OF_PAPER -> PrinterStatus.OutOfPaper
            PRINTER_OVER_HEAT -> PrinterStatus.Overheat
            PRINTER_UNDER_VOLTAGE -> PrinterStatus.UnderVoltage
            PRINTER_BUSY -> PrinterStatus.Busy
            PRINTER_ERROR_DRIVER -> PrinterStatus.DriverError
            else -> PrinterStatus.Error
        }
    }

    private fun initializePrinter(context: Context) {
        val outputDirectory = File(context.filesDir, "fonts/")
        if(outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }

        copyFontToAssets(context, THIN_FONT_NAME, outputDirectory)
        copyFontToAssets(context, NORMAL_FONT_NAME, outputDirectory)
        copyFontToAssets(context, BOLD_FONT_NAME, outputDirectory)
        copyFontToAssets(context, BLACK_FONT_NAME, outputDirectory)
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
            Log.e(TAG, "Urovo printer: font '$fontName' copy failed", e)
            return
        }
    }

    private fun getFontPath(context: Context, fontName: String): String {
        val fontFile = File(context.filesDir, "fonts/$fontName")

        return fontFile.absolutePath
    }

    data class Size(
        val width: Int,
        val height: Int
    )

    private fun measureText(text: String, fontSize: Int, fontPath: String): Size {
        val paint = Paint().apply {
            textSize = fontSize.toFloat()
            typeface = Typeface.createFromFile(fontPath)
        }
        val metrics = paint.fontMetrics

        return Size(
            width = ceil(paint.measureText(text)).roundToInt(),
            height = (metrics.bottom - metrics.top).roundToInt()
        )
    }

    companion object {
        private const val TAG: String = "UROVO-Printer"
        private const val PAPER_WIDTH = 384

        //Printer gray value 0-4
        private const val DEF_PRINTER_HUE_VALUE = 4

        //Print speed value 0-9
        private const val DEF_PRINTER_SPEED_VALUE = 9

        private const val NO_WORD_WRAP: Int = 1

        private const val NO_STYLE: Int = 0
        private const val STYLE_UNDERLINE: Int = 0x04
        private const val STYLE_BOLD: Int = 0x01

        private const val NO_ROTATION: Int = 0

        private const val INFINITE_SIZE: Int = -1

        private const val ORIGIN: Int = 0
        private const val LINE_WIDTH: Int = 2

        private const val PRINTER_OK: Int = 0
        private const val PRINTER_OUT_OF_PAPER = -1
        private const val PRINTER_OVER_HEAT = -2
        private const val PRINTER_UNDER_VOLTAGE = -3
        private const val PRINTER_BUSY = -4
        private const val PRINTER_ERROR = -256
        private const val PRINTER_ERROR_DRIVER = -257


        /* Fonts */
        private const val THIN_FONT_NAME = "jetbrains_mono.ttf"
        private const val NORMAL_FONT_NAME = "jetbrains_mono_medium.ttf"
        private const val BOLD_FONT_NAME = "jetbrains_mono_bold.ttf"
        private const val BLACK_FONT_NAME = "jetbrains_mono_extrabold.ttf"

        private const val DEFAULT_FONT = NORMAL_FONT_NAME

        private const val FONT_SIZE_SMALL: Int = 14
        private const val FONT_SIZE_NORMAL: Int = 16
        private const val FONT_SIZE_LARGE: Int = 28
        private const val FONT_SIZE_EXTRA_LARGE: Int = 34

        private const val DEFAULT_FONT_SIZE = FONT_SIZE_NORMAL
    }
}