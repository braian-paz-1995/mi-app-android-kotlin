package com.ationet.androidterminal.standalone.sale.presentation.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter.formatNumber
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity

@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExit: () -> Unit,
    onConfirmed: () -> Unit
) {
    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = {
                cancelSelection = false
                onExit()
            }
        )
    }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is ConfirmationState.Confirmation -> {
            val quantity = remember {
                if (state.confirmationData.quantity.inputType == Quantity.InputType.Quantity) {
                    state.confirmationData.quantity.value
                } else {
                    state.confirmationData.quantity.value / state.confirmationData.unitPrice
                }
            }

            val amount = remember {
                if (state.confirmationData.quantity.inputType == Quantity.InputType.Amount) {
                    state.confirmationData.quantity.value
                } else {
                    state.confirmationData.quantity.value * state.confirmationData.unitPrice
                }
            }

            val data = mapOf(
                stringResource(R.string.label_product) to state.confirmationData.productName,
                if (state.confirmationData.operationType) {
                    stringResource(R.string.label_quantity) to formatNumber(
                        quantity.toString(),
                        3,
                        state.confirmationData.language
                    ) + " ${state.confirmationData.fuelMeasureUnit}"
                } else {
                    stringResource(R.string.label_quantity) to formatNumber(
                        quantity.toString(),
                        3,
                        state.confirmationData.language
                    )
                },
                stringResource(R.string.label_amount) to "${state.confirmationData.currency} " + formatNumber(
                    amount.toString(),
                    2,
                    state.confirmationData.language
                ),
            )

            ConfirmationContent(
                data = data,
                onBack = onBack,
                onCancel = { cancelSelection = true },
                onConfirm = onConfirmed
            )
        }
    }
}

@Composable
fun ConfirmationContent(
    data: Map<String, String>,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    BackHandler {
        onBack.invoke()
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                onBack = onBack,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        SummaryScreen(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 10.dp, bottom = 6.dp),
            title = stringResource(R.string.confirm_sale),
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AATButton(
                        onClick = onConfirm,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }

                    AATTextButton(
                        onClick = onCancel,
                        text = stringResource(id = R.string.cancel),
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            data = data,
        )
    }
}