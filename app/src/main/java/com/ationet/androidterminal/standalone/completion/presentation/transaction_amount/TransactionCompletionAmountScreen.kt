package com.ationet.androidterminal.standalone.completion.presentation.transaction_amount

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
import com.ationet.androidterminal.core.presentation.NumericPromptScreen
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.presentation.util.handleDecimalTextFieldChange

@Composable
fun TransactionCompletionAmountScreen(
    viewModel: TransactionCompletionAmountViewModel = hiltViewModel(),
    transactionAmount: Double?,
    transactionQuantity: Double?,
    preAuthorizationType: DisplayType,
    currencySymbol: String,
    quantityUnit: String,
    onBack: () -> Unit,
    onTransactionAmountSelected: (Double, DisplayType, Boolean) -> Unit,
    onTransactionQuantitySelected: (Double, DisplayType, Boolean) -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

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
                when (preAuthorizationType) {
                    DisplayType.AMOUNT -> DisplayType.AMOUNT
                    DisplayType.QUANTITY -> DisplayType.QUANTITY
                    DisplayType.FILLUP -> if (transactionAmount != null && transactionQuantity != null) {
                        when (state.isAmount) {
                            true -> DisplayType.AMOUNT
                            else -> DisplayType.QUANTITY
                        }
                    } else if (transactionAmount != null) {
                        DisplayType.AMOUNT
                    } else {
                        DisplayType.QUANTITY
                    }
                }
            )
        }

        var errorText by remember {
            mutableStateOf<String?>(null)
        }

        val amountGreaterThanZero = stringResource(id = R.string.amount_greater_than_zero)
        val quantityGreaterThanZero = stringResource(id = R.string.quantity_greater_than_zero)
        val amountExceeded = stringResource(id = R.string.amount_exceeds_pre_authorized)
        val quantityExceeded = stringResource(id = R.string.quantity_exceeds_pre_authorized)
        val maxDigits = TransactionCompletionAmountViewModel.MAXIMUM_VALUE_PERMITTED
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

                if (displayType == DisplayType.AMOUNT) {
                    errorText = if (value <= 0.0) {
                        amountGreaterThanZero
                    } else if (!state.isEnableFinalizationVariance) {
                        if (transactionAmount != null && value > transactionAmount) {
                            amountExceeded
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    errorText = if (value <= 0.0) {
                        quantityGreaterThanZero
                    } else if (transactionQuantity != null && value > transactionQuantity) {
                        quantityExceeded
                    } else {
                        null
                    }
                }

                // Normalize decimal separator to '.' (We can have both . or , as decimal separators)
                val normalizedValueText = textFieldValue.text.replace(',', '.')

                // Check if the total number of digits (excluding the decimal separator) exceeds the maximum permitted
                if (normalizedValueText.replace(
                        ".",
                        ""
                    ).length > TransactionCompletionAmountViewModel.MAXIMUM_VALUE_PERMITTED
                ) {
                    errorText = valueTooBig
                }
            },
            onDisplayTypeChanged = OnDisplayTypeChanged@{
                displayType = it
                val value = textFieldValue.text.toDoubleOrNull() ?: return@OnDisplayTypeChanged

                if (displayType == DisplayType.AMOUNT) {
                    errorText = if (value <= 0.0) {
                        amountGreaterThanZero
                    } else if (!state.isEnableFinalizationVariance) {
                        if (transactionAmount != null && value > transactionAmount) {
                            amountExceeded
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } else {
                    errorText = if (value <= 0.0) {
                        quantityGreaterThanZero
                    } else if (transactionQuantity != null && value > transactionQuantity) {
                        quantityExceeded
                    } else {
                        null
                    }
                }

                // Normalize decimal separator to '.' (We can have both . or , as decimal separators)
                val normalizedValueText = textFieldValue.text.replace(',', '.')

                // Check if the total number of digits (excluding the decimal separator) exceeds the maximum permitted
                if (normalizedValueText.replace(
                        ".",
                        ""
                    ).length > TransactionCompletionAmountViewModel.MAXIMUM_VALUE_PERMITTED
                ) {
                    errorText = valueTooBig
                }
            },
            onContinue = OnContinue@{
                val value = textFieldValue.text.toDoubleOrNull() ?: return@OnContinue
                if (displayType == DisplayType.AMOUNT) {
                    onTransactionAmountSelected.invoke(value, displayType, state.isCompanyPrice)
                } else {
                    onTransactionQuantitySelected.invoke(value, displayType, state.isCompanyPrice)
                }
            },
            currency = currencySymbol,
            fuelMeasureUnit = quantityUnit,
            showToggle = transactionAmount != null && transactionQuantity != null
        )
    }
}
