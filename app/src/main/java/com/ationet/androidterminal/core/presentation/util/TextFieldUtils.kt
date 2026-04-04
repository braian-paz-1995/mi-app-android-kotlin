package com.ationet.androidterminal.core.presentation.util

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

private const val decimalSeparator = '.'
/**
 * Handles text field input as numeric values.
 * - If text contains non digit values, it trims them.
 * - If text is empty, adds a zero.
 * - If text starts with zero and is followed by non-zero values, removed the leading zero.
 * - It honors the current selection.
 *
 * @param   textFieldValue  Text field to handle
 * */
fun handleDecimalTextFieldChange(
    textFieldValue: TextFieldValue
): TextFieldValue {
    val sb = StringBuilder()
    var start = textFieldValue.selection.start
    var end = textFieldValue.selection.end

    var trim = true
    var decimalAppeared = false

    for (i in textFieldValue.text.indices) {
        val char = textFieldValue.text[i]

        if (!char.isDigit() && char != decimalSeparator) {
            if (i <= start) {
                start--
                end--
            }
            continue
        }

        if (char == '0' && trim) {
            if (i <= start) {
                start--
                end--
            }
            continue
        } else {
            trim = false
        }

        if (char == decimalSeparator && decimalAppeared) {
            if (i <= start) {
                start--
                end--
            }
            continue
        } else if (char == decimalSeparator) {
            decimalAppeared = true
        }

        sb.append(char)
    }

    if (sb.isEmpty()) {
        sb.append('0')
        start = 1
        end = 1
    }


    val text = sb.toString()
    val safeStart = start.coerceIn(0, text.length)
    val safeEnd = end.coerceIn(safeStart, text.length)

    return textFieldValue.copy(
        text = text,
        selection = TextRange(safeStart, safeEnd)
    )
}
