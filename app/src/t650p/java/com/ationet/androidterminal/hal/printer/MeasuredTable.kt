package com.ationet.androidterminal.hal.printer

import com.ationet.androidterminal.core.domain.hal.printer.Alignment
import com.ationet.androidterminal.core.domain.hal.printer.HALPrinter
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class MeasuredTable(
    table: HALPrinter.Table,
    maxCharacterPerLine: Int,
) {
    data class MeasuredCell(
        val text: String,
        val alignment: Alignment,
        val textWidth: Int,
        val width: Int
    )

    data class MeasuredRow(
        val cells: List<MeasuredCell>
    )

    val rows: List<MeasuredRow>

    init {
        rows = measureTable(table, maxCharacterPerLine)
    }

    private fun measureTable(
        table: HALPrinter.Table,
        maxCharacterPerLine: Int,
    ): List<MeasuredRow> {
        /* Measure column width */
        val columns = measureColumns(maxCharacterPerLine, table.header)

        /* Measure header elements */
        val headerCells = measureRow(table.header)
        for (columnIndex in table.header.columns) {
            val column = columns[columnIndex]

            val cell = headerCells[columnIndex]
            headerCells[columnIndex] = cell.copy(
                width = column.width
            )
        }

        val measuredRows = mutableListOf<MeasuredRow>()

        measuredRows += MeasuredRow(headerCells)

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

            measuredRows += MeasuredRow(rowCells)
        }

        return measuredRows
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
                val fractionSize = floor(width * column.fraction).roundToInt()

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

    private fun <T: HALPrinter.Cell> measureRow(
        row: HALPrinter.Row<T>
    ): MutableList<MeasuredCell> {
        val measuredCells = mutableListOf<MeasuredCell>()
        for (cell in row.cells) {
            measuredCells += measureCell(cell)
        }

        return measuredCells
    }

    private fun measureCell(
        cell: HALPrinter.Cell
    ): MeasuredCell {
        return MeasuredCell(
            text = cell.text,
            textWidth = cell.text.length,
            width = cell.text.length,
            alignment = cell.alignment,
        )
    }
}