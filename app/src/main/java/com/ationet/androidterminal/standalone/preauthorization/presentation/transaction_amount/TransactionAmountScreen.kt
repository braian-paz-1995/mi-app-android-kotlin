package com.ationet.androidterminal.standalone.preauthorization.presentation.transaction_amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.presentation.NumericPromptScreen
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.presentation.util.handleDecimalTextFieldChange

@Composable
fun TransactionAmountScreen(
    viewModel: TransactionAmountViewModel = hiltViewModel(),
    onTransactionCompleted: () -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    TransactionAmountContent(
        onBack = onBack,
        onTransactionAmountSelected = {
            viewModel.setTransaction(it, InputType.Amount)
            onTransactionCompleted()
        },
        onTransactionQuantitySelected = {
            viewModel.setTransaction(it, InputType.Quantity)
            onTransactionCompleted()
        },
        state = state
    )
}

@Composable
fun TransactionAmountContent(
    onBack: () -> Unit,
    onTransactionAmountSelected: (Double) -> Unit,
    onTransactionQuantitySelected: (Double) -> Unit,
    state: TransactionAmountState
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

        var displayType by remember {
            mutableStateOf(
                when (state.isAmount) {
                    true -> DisplayType.AMOUNT
                    else -> DisplayType.QUANTITY
                }
            )
        }

        var errorText by remember {
            mutableStateOf<String?>(null)
        }

        val amountGreaterThanZero = stringResource(id = R.string.amount_greater_than_zero)
        val quantityGreaterThanZero = stringResource(id = R.string.quantity_greater_than_zero)
        val maxDigits = TransactionAmountViewModel.MAXIMUM_VALUE_PERMITTED
        val valueTooBig = stringResource(id = R.string.value_too_big, maxDigits)

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

                errorText = if (displayType == DisplayType.AMOUNT) {
                    if (value <= 0.0) {
                        amountGreaterThanZero
                    } else {
                        null
                    }
                } else {
                    if (value <= 0.0) {
                        quantityGreaterThanZero
                    } else {
                        null
                    }
                }

                // Normalize decimal separator to '.' (We can have both . or , as decimal separators)
                val normalizedValueText = textFieldValue.text.replace(',', '.')

                // Check if the total number of digits (excluding the decimal separator) exceeds the maximum permitted
                if (normalizedValueText.replace(".", "").length > TransactionAmountViewModel.MAXIMUM_VALUE_PERMITTED) {
                    errorText= valueTooBig
                }
            },
            onDisplayTypeChanged = {
                displayType = it
            },
            currency = state.currency,
            fuelMeasureUnit = state.fuelMeasureUnit,
            onContinue = OnContinue@{
                val value = textFieldValue.text.toDoubleOrNull() ?: return@OnContinue
                if (displayType == DisplayType.AMOUNT) {
                    onTransactionAmountSelected.invoke(value)
                } else {
                    onTransactionQuantitySelected.invoke(value)
                }
            }
        )
    }
}