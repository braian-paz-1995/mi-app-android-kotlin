package com.ationet.androidterminal.standalone.balance_enquiry.presentation.transaction_process

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKDescription
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkAuthorizationCode
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun TransactionProcessScreen(
    viewModel: TransactionProcessViewModel = hiltViewModel(),
    onSuccess: (summaryData: SummaryData) -> Unit,
    onFailure: () -> Unit,
) {

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is TransactionProcessState.LoadingTransaction -> {
            LoadingScreen(
                loadingState = state.loadingState,
                onSuccess = {
                    if (state.summaryData != null) {
                        onSuccess(state.summaryData)
                    }
                },
                onFailure = { }
            )
        }


        is TransactionProcessState.TransactionProcessError -> {
            LaunchedEffect(true) {
                delay(2.seconds)
                onFailure()
            }
            ProcessTransactionNOK(
                messageError = state.message
            )
        }

        TransactionProcessState.CommunicationError -> {
            LaunchedEffect(Unit) {
                delay(2.seconds)
                onFailure()
            }

            CommunicationErrorScreen(
                onExit = onFailure
            )
        }
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
            text = stringResource(R.string.your_sale_has_been_completed),
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
