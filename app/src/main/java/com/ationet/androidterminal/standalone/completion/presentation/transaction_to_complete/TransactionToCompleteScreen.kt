package com.ationet.androidterminal.standalone.completion.presentation.transaction_to_complete

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
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationData

@Composable
fun TransactionToCompleteScreen(
    viewModel: TransactionToCompleteViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNext: (PreAuthorizationData) -> Unit,
    onExit: () -> Unit,
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        TransactionToCompleteState.Loading -> TransactionLoadingScreen()
        TransactionToCompleteState.NoTransaction -> NoTransactionScreen(
            onExit = onExit
        )

        is TransactionToCompleteState.Transaction -> TransactionDetailsScreen(
            authorizationCode = state.preAuthorizationData.authorizationCode,
            product = state.preAuthorizationData.product.name,
            authorizedAmount = state.preAuthorizationData.amount,
            authorizedQuantity = state.preAuthorizationData.quantity,
            currencySymbol = state.preAuthorizationData.currencySymbol,
            quantityUnit = state.preAuthorizationData.quantityUnit,
            onBack = onBack,
            onExit = onExit,
            onNext = { onNext.invoke(state.preAuthorizationData) }
        )
    }
}

@Composable
private fun TransactionLoadingScreen() {
    LoadingScreen(
        loadingState = LoadingState.Loading,
        onSuccess = { },
        onFailure = { }
    )
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
            title = stringResource(R.string.no_transactions_to_complete),
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
    product: String,
    authorizedQuantity: Double?,
    authorizedAmount: Double?,
    currencySymbol: String,
    quantityUnit: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onExit: () -> Unit,
) {
    BackHandler { }

    var cancelRead by remember {
        mutableStateOf(false)
    }
    if (cancelRead) {
        Alert(
            onDismissDialog = { cancelRead = false },
            onConfirmDialog = onExit
        )
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        val authCodeLabel = stringResource(R.string.pre_authorization_auth_code)
        val productNameLabel = stringResource(R.string.product)
        val authorizedQuantityLabel = stringResource(R.string.authorized_quantity)
        val authorizedAmountLabel = stringResource(R.string.authorized_amount)
        val context = LocalContext.current

        val summaryData = remember {
            buildMap {
                put(authCodeLabel, authorizationCode)
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
            title = stringResource(R.string.transaction_to_complete),
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
                        Text(stringResource(id = R.string.next))
                    }

                    AATTextButton(
                        onClick = { cancelRead = true },
                        text = stringResource(id = R.string.cancel),
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                }

            },
            data = summaryData,
        )
    }
}