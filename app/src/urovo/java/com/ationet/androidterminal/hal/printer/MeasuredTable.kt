package com.ationet.androidterminal.hal.printer

import com.ationet.androidterminal.core.domain.hal.printer.Alignment
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import com.ationet.androidterminal.hal.printer.HALPrinterImpl.Size
import kotlin.math.ceil
import kotlin.math.roundToInt

class MeasuredTable(
    table: HALPrinter.Table,
    paperWidth: Int,
    private val measureCell: (HALPrinter.Cell) -> MeasuredCell
) {
    data class MeasuredCell(
        val text: String,
        val alignment: Alignment,
        val textSize: Size,
        val width: Int,
        val height: Int,
        val fontPath: String,
        val fontSize: Int
    )

    data class MeasuredRow(
        val height: Int,
        val cells: List<MeasuredCell>
    )

    val rows: List<MeasuredRow>

    init {
        rows = measureTable(table, paperWidth)
    }

    private fun measureTable(
        table: HALPrinter.Table,
        paperWidth: Int,
    ): List<MeasuredRow> {
        /* Measure column width */
        val columns = measureColumns(paperWidth, table.header)

        val measuredRows = mutableListOf<MeasuredRow>()

        /* Measure header elements */
        val headerCells = measureRow(table.header)
        for (columnIndex in table.header.columns) {
            val column = columns[columnIndex]

            val cell = headerCells[columnIndex]
            headerCells[columnIndex] = cell.copy(
                width = column.width
            )
        }

        val headerRowHeight = headerCells.maxOf { it.height }
        measuredRows += MeasuredRow(
            height = headerRowHeight,
            cells = headerCells
        )

        for (rowIndex in table.rows.indices) {
            val row = table.rows[rowIndex]

            val rowCells = measureRow(row)

            for (columnIndex in row.cells.indices) {
                val column = columns[columnIndex]

                val cell = rowCells[columnIndex]
                rowCells[columnIndex] = cell.copy(
                    width = column.width
                )
            }

            val rowHeight = rowCells.maxOf { it.height }
            measuredRows += MeasuredRow(
                height = rowHeight,
                cells = rowCells
            )
        }

        return measuredRows.toList()
    }

    private data class MeasuredColumn(
        var width: Int,
    )

    private fun measureColumns(
        width: Int,
        header: HALPrinter.Row<HALPrinter.Header>
    ): List<MeasuredColumn> {
        require(header.cells.sumOf { it.fraction } <= 1) { "Sum of fractions exceeds max amount (val = 1)" }

        val measuredColumns = header.cells.map {
            MeasuredColumn(0)
        }

        var remainingSize = width
        var columnsWithFixedWidth = 0
        for (columnIndex in header.columns) {
            val column = header.cells[columnIndex]

            if(column.fraction > 0.0) {
                /* It takes a fixed fraction of the total width
                (that's why we had to check the sum of fractions is well distributed) */
                val fractionSize = ceil(width * column.fraction).roundToInt()

                measuredColumns[columnIndex].width = fractionSize

                remainingSize -= fractionSize

                columnsWithFixedWidth++
            }
        }

        val remainingColumns = header.cells.size - columnsWithFixedWidth
        val remainingColumnWidth = ceil(remainingSize * 1.0 / remainingColumns).roundToInt()

        for (columnIndex in header.columns) {
            val column = header.cells[columnIndex]

            if(column.fraction == 0.0) {
                val size = minOf(remainingSize, remainingColumnWidth)
                measuredColumns[columnIndex].width = size
                remainingSize -= size
            }
        }

        return measuredColumns
    }

    private fun <T: HALPrinter.Cell>measureRow(
        row: HALPrinter.Row<T>
    ): MutableList<MeasuredCell> {
        val measuredCells = mutableListOf<MeasuredCell>()
        for (cell in row.cells) {
            measuredCells += measureCell.invoke(cell)
        }

        return measuredCells
    }
}