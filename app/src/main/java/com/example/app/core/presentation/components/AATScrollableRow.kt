package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R

@Composable
fun ScrollableRow(
    buttons: List<ButtonData>,
    isButtonDisabled: Boolean,
    isPendingEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        buttons.forEach { buttonData ->
            val isPendingTransactionButton =
                buttonData.title == stringResource(R.string.pending_transactions)
            CircleButton(
                iconRes = buttonData.iconRes,
                title = buttonData.title,
                size = buttonData.size,
                fontSize = buttonData.fontSize,
                lineHeight = buttonData.lineHeight,
                isEnabled = if (isPendingTransactionButton) isPendingEnabled else true,
                onClick = {
                    if (!isButtonDisabled && (!isPendingTransactionButton || (isPendingTransactionButton && isPendingEnabled))) {
                        buttonData.onClick()
                    }
                }
            )
        }
    }
}