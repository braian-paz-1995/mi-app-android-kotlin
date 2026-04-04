package com.ationet.androidterminal.standalone.sale.presentation.sku.sku_transaction_amount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.util.handleDecimalTextFieldChange
import com.ationet.androidterminal.standalone.sale.domain.model.Quantity


@Composable
fun SKUTransactionAmountScreen(
    viewModel: TransactionAmountViewModel = hiltViewModel(),
    onTransactionCompleted: () -> Unit,
    onBack: () -> Unit
) {
    TransactionAmountContent(
        onBack = onBack,
        onTransactionQuantitySelected = {
            viewModel.setTransaction(it, Quantity.InputType.Quantity)
            onTransactionCompleted()
        }
    )
}

@Composable
fun TransactionAmountContent(
    onBack: () -> Unit,
    onTransactionQuantitySelected: (Double) -> Unit
) {
    BackHandler { onBack.invoke() }

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
        var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        var errorText by remember { mutableStateOf<String?>(null) }

        val quantityGreaterThanZero = stringResource(id = R.string.quantity_greater_than_zero)
        val maxDigits = TransactionAmountViewModel.MAXIMUM_VALUE_PERMITTED
        val valueTooBig = stringResource(id = R.string.value_too_big, maxDigits)

        NumericPromptScreen(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(R.string.enter_quantity),
            value = textFieldValue.text,
            onValueChanged = { newValue ->
                textFieldValue = handleDecimalTextFieldChange(TextFieldValue(newValue))
                val value = newValue.toDoubleOrNull() ?: return@NumericPromptScreen

                errorText = when {
                    value <= 0.0 -> quantityGreaterThanZero
                    newValue.replace(".", "").length > maxDigits -> valueTooBig
                    else -> null
                }
            },
            isError = !errorText.isNullOrEmpty(),
            supportingText = errorText,
            buttons = {
                AATButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        val value = textFieldValue.text.toDoubleOrNull() ?: return@AATButton
                        onTransactionQuantitySelected.invoke(value)
                    },
                    enabled = textFieldValue.text.isNotEmpty() && errorText.isNullOrEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.continue_button),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        )
    }
}