package com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime

@Composable
fun TransactionConfirmationScreen(
    viewModel: TransactionConfirmationViewModel = hiltViewModel(),
    onClearIt: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000L)
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BackHandler { }

            CircularProgressIndicator()
        }
    } else {
        when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
            TransactionConfirmationState.NoTransactionFound -> NoTransactionScreen(
                onExit = onExit
            )

            is TransactionConfirmationState.TransactionConfirmation -> {
                TransactionDetailsScreen(
                    authorizationCode = state.data.authorizationData.authorizationCode,
                    date = state.data.authorizationData.date,
                    product = state.data.authorizationData.product.name,
                    authorizedQuantity = state.data.authorizationData.quantity,
                    authorizedAmount = state.data.authorizationData.amount,
                    currencySymbol = state.data.authorizationData.currencySymbol,
                    quantityUnit = state.data.authorizationData.quantityUnit,
                    onBack = onBack,
                    onNext = onClearIt,
                    onExit = onExit
                )
            }
        }
    }

}

@Composable
private fun NoTransactionScreen(
    onExit: () -> Unit,
) {
    BackHandler { }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) { paddingValues ->
        InfoScreenTemplate(
            modifier = Modifier
                .padding(paddingValues)
                .padding(bottom = 30.dp),
            title = stringResource(R.string.no_pending_transactions),
            imageRes = R.drawable.empty_box,
            imageSize = 230.dp,
            description = stringResource(R.string.no_transactions_to_complete_message),
            buttonText = stringResource(R.string.okay_exit),
            onConfirmClick = onExit,
            onCancelClick = { }
        )
    }
}

@Composable
private fun TransactionDetailsScreen(
    authorizationCode: String,
    date: LocalDateTime,
    product: String,
    authorizedQuantity: Double?,
    authorizedAmount: Double?,
    currencySymbol: String,
    quantityUnit: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val formattedDate = remember(date) {
        LocaleFormatter.formatDateTime(dateTime = date, context = context)
    }

    var cancelRead by remember {
        mutableStateOf(false)
    }

    if (cancelRead) {
        Alert(
            onDismissDialog = { cancelRead = false },
            onConfirmDialog = onExit
        )
    }

    BackHandler { }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        val authCodeLabel = stringResource(R.string.pre_authorization_auth_code)
        val dateLabel = stringResource(R.string.label_date)
        val productNameLabel = stringResource(R.string.product)
        val authorizedQuantityLabel = stringResource(R.string.authorized_quantity)
        val authorizedAmountLabel = stringResource(R.string.authorized_amount)

        val summaryData = remember {
            buildMap {
                put(authCodeLabel, authorizationCode)
                put(dateLabel, formattedDate)
                put(productNameLabel, product)

                if (authorizedQuantity != null) {
                    put(authorizedQuantityLabel, LocaleFormatter.formatNumber(authorizedQuantity.toString(), 3, context) + " $quantityUnit")
                }

                if (authorizedAmount != null) {
                    put(authorizedAmountLabel, "$currencySymbol " + LocaleFormatter.formatNumber(authorizedAmount.toString(), 2, context))
                }
            }
        }
        SummaryScreen(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 10.dp),
            title = stringResource(R.string.pending_transaction),
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AATButton(
                        onClick = onNext,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.clear_it))
                    }

                    AATTextButton(
                        onClick = { cancelRead = true },
                        text = stringResource(id = R.string.cancel),
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            },
            data = summaryData,
        )
    }
}