package com.ationet.androidterminal.standalone.sale.presentation.transaction_amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.NumericPromptScreen
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.presentation.util.handleDecimalTextFieldChange
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity

@Composable
fun TransactionAmountScreen(
    viewModel: TransactionAmountViewModel = hiltViewModel(),
    onTransactionCompleted: () -> Unit,
    onBack: () -> Unit
) {
    val transactionAmount by remember { mutableDoubleStateOf(Double.MAX_VALUE) }
    val transactionQuantity by remember { mutableDoubleStateOf(Double.MAX_VALUE) }


    TransactionAmountContent(
        transactionAmount = transactionAmount,
        transactionQuantity = transactionQuantity,
        onBack = onBack,
        onTransactionAmountSelected = {
            viewModel.setTransaction(it, Quantity.InputType.Amount)
            onTransactionCompleted()
        },
        onTransactionQuantitySelected = {
            viewModel.setTransaction(it, Quantity.InputType.Quantity)
            onTransactionCompleted()
        },
        currency = viewModel.currency,
        fuelMeasureUnit = viewModel.fuelMeasureUnit,
        isAmount = viewModel.isAmount
    )
}

@Composable
fun TransactionAmountContent(
    transactionAmount: Double,
    transactionQuantity: Double,
    onBack: () -> Unit,
    onTransactionAmountSelected: (Double) -> Unit,
    onTransactionQuantitySelected: (Double) -> Unit,
    currency: String,
    fuelMeasureUnit: String,
    isAmount: Boolean
) {
    BackHandler {
        onBack.invoke()
    }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                shouldDisplayLogoIcon = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(""))
        }

        var errorText by remember {
            mutableStateOf<String?>(null)
        }

        var displayType by remember {
            mutableStateOf(
                when (isAmount) {
                    true -> DisplayType.AMOUNT
                    else -> DisplayType.QUANTITY
                }
            )
        }

        val amountGreaterThanZero = stringResource(id = R.string.amount_greater_than_zero)
        val quantityGreaterThanZero = stringResource(id = R.string.quantity_greater_than_zero)
        val amountExceeded = stringResource(id = R.string.amount_exceeds_pre_authorized)
        val quantityExceeded = stringResource(id = R.string.quantity_exceeds_pre_authorized)

        NumericPromptScreen(
            modifier = Modifier
                .padding(innerPadding),
            title = stringResource(R.string.enter_value),
            displayType = displayType,
            value = textFieldValue,
            errorText = errorText,
            onValueChanged = OnValueChanged@{
                /* Only changed cursor */
                if (it.text == textFieldValue.text) {
                    textFieldValue = it
                    return@OnValueChanged
                }

                textFieldValue = handleDecimalTextFieldChange(
                    textFieldValue = it
                )

                val value = textFieldValue.text.toDoubleOrNull() ?: return@OnValueChanged

                if (displayType == DisplayType.AMOUNT) {
                    errorText = if (value <= 0.0) {
                        amountGreaterThanZero
                    } else if (value > transactionAmount) {
                        amountExceeded
                    } else {
                        null
                    }
                } else {
                    errorText = if (value <= 0.0) {
                        quantityGreaterThanZero
                    } else if (value > transactionQuantity) {
                        quantityExceeded
                    } else {
                        null
                    }
                }
            },
            onDisplayTypeChanged = {
                displayType = it
            },
            onContinue = OnContinue@{
                val value = textFieldValue.text.toDoubleOrNull() ?: return@OnContinue
                if (displayType == DisplayType.AMOUNT) {
                    onTransactionAmountSelected.invoke(value)
                } else {
                    onTransactionQuantitySelected.invoke(value)
                }
            },
            currency = currency,
            fuelMeasureUnit = fuelMeasureUnit
        )
    }
}