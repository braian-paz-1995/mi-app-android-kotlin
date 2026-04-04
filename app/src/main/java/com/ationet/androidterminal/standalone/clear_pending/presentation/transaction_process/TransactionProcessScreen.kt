package com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_process

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
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun TransactionProcessScreen(
    viewModel: TransactionProcessViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is TransactionProcessState.LoadingTransaction -> {
            LoadingScreen(
                loadingState = state.loadingState,
                onSuccess = { },
                onFailure = { }
            )
        }

        is TransactionProcessState.TransactionProcessError -> {
            LaunchedEffect(true) {
                delay(4000)
                onFailure()
            }
            ProcessTransactionNOK(
                messageError = state.message
            )
        }

        is TransactionProcessState.TransactionProcessOk -> {
            LaunchedEffect(true) {
                delay(4000)
                onSuccess()
            }
            ProcessTransactionOK(
                authorizationCode = state.authorizationCode
            )
        }

        TransactionProcessState.CommunicationError -> {
            LaunchedEffect(Unit) {
                delay(3.seconds)
                onFailure()
            }

            val message = stringResource(R.string.ationet_communication_error)
            CommunicationFailedScreen(
                errorCode = null,
                message = message,
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
            text = stringResource(R.string.your_transaction_has_been_cleared),
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
private fun CommunicationFailedScreen(
    errorCode: String?,
    message: String,
) {
    BackHandler { }

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
                    description = message,
                    auxiliaryText = errorCode.orEmpty(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}