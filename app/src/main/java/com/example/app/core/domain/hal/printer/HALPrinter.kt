package com.ationet.androidterminal.core.domain.hal.printer

import android.graphics.Bitmap

interface HALPrinter {
    /**
     * Gets current printer status
     * */
    val status: PrinterStatus
    /**
     * Writes text on a printer page
     *
     * @param text the text to be rendered on page.
     * @param format the TextFormat object specifying the format settings for the text.
     * @return Boolean indicating whether the text was successfully written.
     * */
    fun write(
        text: String,
        format: TextFormat = TextFormat(),
    ): Boolean
    /**
     * Draws a dotted line
     *
     * @param format the TextFormat object specifying the format settings for the line.
     * @return Boolean indicating whether the line was successfully drawn.
     * */
    fun drawLine(
        format: TextFormat = TextFormat()
    ): Boolean

    /**
     * Draws an image into a printer page.
     *
     * @param image the Bitmap object representing the image to be drawn.
     * @param format the ImageFormat object specifying the format settings for the image.
     * @return Boolean indicating whether the image was successfully drawn.
     */
    fun drawImage(
        image: Bitmap,
        format: ImageFormat = ImageFormat()
    ): Boolean

    /**
     * Begins drawing a table
     *
     * @param init   The instructions to build the table
     * */
    fun drawTable(
        init: Table.() -> Unit,
    ): Boolean

    @DslMarker
    @Target(AnnotationTarget.TYPE)
    annotation class TableBuilderMarker

    open class Cell(
        val text: String,
        val alignment: Alignment,
    )

    class Header(
        text: String,
        alignment: Alignment,
        val fraction: Double
    ): Cell(text, alignment)

    data class Row<T: Cell> (
        val cells: List<T>,
    ) {
        val isEmpty: Boolean = cells.isEmpty()
        val columns : IntRange = cells.indices
    }

    class Table {
        private var _header = Row<Header>(emptyList())
        val header : Row<Header> get() = _header
        private var _rows: MutableList<Row<Cell>> = mutableListOf()
        val rows: List<Row<Cell>> get() = _rows.toList()

        class HeaderRowBuilder {
            private val cells: MutableList<Header> = mutableListOf()

            fun build(): Row<Header> {
                return Row(cells)
            }

            fun header(
                text: String,
                fraction: Double = 0.0,
                alignment: Alignment = Alignment.Center,
            ) {
                addCell(
                    cell = Header(
                        text = text,
                        alignment = alignment,
                        fraction = fraction
                    )
                )
            }

            private fun addCell(cell: Header) {
                cells += cell
            }
        }

        class DataRowBuilder {
            private val cells: MutableList<Cell> = mutableListOf()

            fun build(): Row<Cell> {
                return Row(cells)
            }

            fun cell(
                text: String,
                alignment: Alignment = Alignment.Center,
            ) {
                cell(
                    cell = Cell(
                        text = text,
                        alignment = alignment,
                    )
                )
            }

            private fun cell(cell: Cell) {
                cells += cell
            }
        }

        fun headers(
            init: @TableBuilderMarker HeaderRowBuilder.() -> Unit
        ) {
            val builder = HeaderRowBuilder()
            builder.init()
            _header = builder.build()
        }

        fun row(
            init: @TableBuilderMarker DataRowBuilder.() -> Unit
        ) {
            val builder = DataRowBuilder()
            builder.init()

            _rows += builder.build()
        }

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Cannot add headers inside data row", level = DeprecationLevel.ERROR)
        fun DataRowBuilder.headers(
            init: @TableBuilderMarker HeaderRowBuilder.() -> Unit
        ) {
            error("Cannot add headers inside data row")
        }

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Cannot add nested rows", level = DeprecationLevel.ERROR)
        fun DataRowBuilder.row(
            init: @TableBuilderMarker DataRowBuilder.() -> Unit
        ) {
            error("Cannot add nested rows")
        }

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Cannot add rows inside headers", level = DeprecationLevel.ERROR)
        fun HeaderRowBuilder.row(
            init: @TableBuilderMarker DataRowBuilder.() -> Unit
        ) {
            error("Cannot add rows inside headers")
        }

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated("Cannot add nested headers", level = DeprecationLevel.ERROR)
        fun HeaderRowBuilder.headers(
            init: @TableBuilderMarker HeaderRowBuilder.() -> Unit
        ) {
            error("Cannot add nested headers")
        }
    }

    /**
     * Feeds paper to the printer.
     *
     * @param   steps   amount of steps of paper to feed the printer.
     * @return  Boolean indicating whether the paper was fed.
     * */
    fun feedPaper(steps: Int = 1): Boolean

    /**
     * Prints page
     *
     * @param feedSteps the amount of steps to feed the printer.
     * @return Status of the printer
     * */
    suspend fun print(feedSteps: Int = 1) : PrinterStatus
}