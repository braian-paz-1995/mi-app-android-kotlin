package com.ationet.androidterminal.core.data.local.util

fun String.splitWordLimit(
    limit: Int,
    delimiter: Char = ' ',
): List<String> {
    val linesList = mutableListOf<String>()

    var lineIndex = 0
    var lastDelimiterIndex = 0
    var index = 0
    while (index < length) {
        /* Current word exceeds line limit? */
        if (index - lineIndex> limit) {
            /* Copy from the beginning of the length until the last word boundary index */
            val range = lineIndex until lastDelimiterIndex

            val line = if(range.isEmpty()) {
                /* Line index and last delimiter matched. So, this would produce a empty line.
                * To avoid this, copy up to this point. */
                lineIndex = index
                substring(lineIndex until index)
            } else {
                // Skip delimiter from the beginning of the string
                lastDelimiterIndex++
                lineIndex = lastDelimiterIndex
                substring(range)
            }

            linesList += line

            index++
            continue
        }

        /* Delimiter found? */
        if (this[index] != delimiter) {
            index++
            continue
        }

        lastDelimiterIndex = index
        index++
    }

    if(index - lineIndex > 0) {
        val lastLine = substring(lineIndex until  index)

        linesList += lastLine
    }

    return linesList.toList()
}

fun centerText(input: String, maxLength: Int) : String {
    val trimmedInput = input.substring(0 until minOf(input.length, maxLength))

    val padStartLength = ((maxLength - trimmedInput.length) / 2)
    val padEndLength = (maxLength - trimmedInput.length - padStartLength).coerceAtLeast(0)
    return " ".repeat(padStartLength) + trimmedInput + " ".repeat(padEndLength)
}