package com.ationet.androidterminal.standalone.void_transaction.presentation.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.domain.model.transaction.TransactionType
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKDescription
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkAuthorizationCode
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSuccess: (authorizationCode: String) -> Unit,
    onFailure: () -> Unit
) {
    BackHandler { onBack() }

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
            is ConfirmationState.Confirmation -> {
                val quantityOrAmount = LocaleFormatter.formatNumber(
                    state.confirmationData.quantity.value.toString(),
                    if (state.confirmationData.quantity.inputType == InputType.Amount) 2 else 3, state.confirmationData.language
                )

                val transactionType = when(state.confirmationData.transactionType){
                    TransactionType.Sale.toString() -> stringResource(R.string.sale)
                    TransactionType.Completion.toString() -> stringResource(R.string.completion)
                    TransactionType.PreAuthorization.toString() -> stringResource(R.string.pre_authorization)
                    else -> ""
                }

                ConfirmationContent(
                    data = mapOf(
                        stringResource(R.string.label_date) to LocaleFormatter.formatDate(
                            dateTime = state.confirmationData.date,
                            context = LocalContext.current,
                        ),
                        stringResource(R.string.auth_code) to state.confirmationData.authorizationCode,
                        stringResource(R.string.transaction_type) to transactionType,
                        stringResource(
                            if (state.confirmationData.quantity.inputType == InputType.Amount)
                                R.string.label_amount_completed
                            else
                                R.string.label_quantity_completed
                        ) to (
                                if (state.confirmationData.quantity.inputType == InputType.Amount)
                                    ("${state.confirmationData.currency} $quantityOrAmount")
                                else
                                    ("$quantityOrAmount ${state.confirmationData.fuelMeasureUnit}")
                                ),
                        stringResource(
                            if (state.confirmationData.quantity.inputType == InputType.Amount) {
                                R.string.quantity
                            } else {
                                R.string.amount
                            }
                        ) to (
                                if (state.confirmationData.quantity.inputType == InputType.Amount) {
                                    val quantityCalculated = state.confirmationData.quantity.value / state.confirmationData.unitPrice
                                    "${LocaleFormatter.formatNumber(quantityCalculated.toString(), 3, state.confirmationData.language)} ${state.confirmationData.fuelMeasureUnit}"
                                } else {
                                    val amountCalculated = state.confirmationData.quantity.value * state.confirmationData.unitPrice
                                    "${state.confirmationData.currency} ${LocaleFormatter.formatNumber(amountCalculated.toString(), 2, state.confirmationData.language)}"
                                }
                        )
                    ),
                    onBack = onBack,
                    onExit = onExit,
                    onConfirm = {
                        viewModel.onConfirm()
                    }
                )
            }

            is ConfirmationState.LoadingTransaction -> {
                LoadingScreen(
                    loadingState = state.loadingState,
                    onSuccess = { },
                    onFailure = { }
                )
            }

            is ConfirmationState.TransactionProcessError -> {
                LaunchedEffect(true) {
                    delay(2.seconds)
                    onFailure()
                }
                ProcessTransactionNOK(
                    messageError = state.message
                )
            }

            is ConfirmationState.TransactionProcessOk -> {
                LaunchedEffect(true) {
                    delay(2.seconds)
                    onSuccess(state.authorizationCode)
                }
                ProcessTransactionOK(
                    authorizationCode = state.authorizationCode
                )
            }

            ConfirmationState.CommunicationError -> {
                LaunchedEffect(Unit) {
                    delay(3.seconds)
                    onFailure()
                }

                CommunicationErrorScreen(onExit)
            }
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
            title = stringResource(R.string.transaction_to_void),
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
                        Text(stringResource(R.string.confirm_void))
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

@Composable
private fun ProcessTransactionNOK(
    messageError: String
) {
    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        ProcessNOKScreen(
            text = stringResource(R.string.something_went_wrong),
            subText = stringResource(R.string.the_operation_was_cancelled),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {
                ProcessNOKDescription(
                    description = messageError,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}

@Composable
private fun ProcessTransactionOK(
    authorizationCode: String
) {
    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        ProcessOkScreen(
            text = stringResource(R.string.your_transaction_has_been_voided),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {
                ProcessOkAuthorizationCode(
                    authorizationCode = authorizationCode,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
        )
    }
}

@Composable
private fun CommunicationErrorScreen(
    onExit: () -> Unit
) {
    BackHandler {
        onExit.invoke()
    }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        ProcessNOKScreen(
            text = stringResource(R.string.something_went_wrong),
            subText = stringResource(R.string.the_operation_was_cancelled),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {
                ProcessNOKTwoLineDescription(
                    description = stringResource(R.string.ationet_communication_error),
                    auxiliaryText = "",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}