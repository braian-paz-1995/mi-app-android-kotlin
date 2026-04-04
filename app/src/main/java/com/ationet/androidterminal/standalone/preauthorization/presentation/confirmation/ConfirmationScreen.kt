package com.ationet.androidterminal.standalone.preauthorization.presentation.confirmation

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter

@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExit: () -> Unit,
    onConfirmed: () -> Unit
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is ConfirmationState.Confirmation -> {
            val quantityOrAmount = LocaleFormatter.formatNumber(
                state.confirmationData.quantity.value.toString(),
                if (state.confirmationData.quantity.inputType == InputType.Amount) 2 else 3, state.confirmationData.language
            )

            ConfirmationContent(
                data = mapOf(
                    stringResource(R.string.label_date) to LocaleFormatter.formatDate(
                        dateTime = state.confirmationData.date,
                        context = LocalContext.current,
                    ),
                    stringResource(R.string.product) to state.confirmationData.productName,
                    stringResource(
                        if (state.confirmationData.quantity.inputType == InputType.Amount)
                            R.string.label_amount
                        else
                            R.string.label_quantity
                    ) to (
                            if (state.confirmationData.quantity.inputType == InputType.Amount)
                                ("${state.confirmationData.currency} $quantityOrAmount")
                            else
                                ("$quantityOrAmount ${state.confirmationData.fuelMeasureUnit}")
                            )
                ),
                onBack = onBack,
                onExit = onExit,
                onConfirm = onConfirmed
            )
        }

        is ConfirmationState.ConfirmationFillUp -> {
            ConfirmationContent(
                data = mapOf(
                    stringResource(R.string.label_date) to LocaleFormatter.formatDate(
                        state.confirmationData.date,
                        LocalContext.current
                    ),
                    stringResource(R.string.product) to state.confirmationData.productName,
                ),
                onBack = onBack,
                onExit = onExit,
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
    onExit: () -> Unit,
) {
    BackHandler {
        onBack.invoke()
    }

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
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
                .padding(top = 10.dp, end = 10.dp, start = 10.dp, bottom = 6.dp),
            title = stringResource(R.string.transaction_to_pre_authorize),
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    AATButton(
                        onClick = onConfirm,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }

                AATTextButton(
                    onClick = { cancelSelection = true },
                    text = stringResource(id = R.string.cancel),
                    textColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            data = data,
        )
    }
}
