package com.ationet.androidterminal.standalone.batch_close.presentation.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKDescription
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.ui.theme.AATIcons
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ConfirmationScreen(
    viewModel: ConfirmationViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSuccess: () -> Unit,
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
                ConfirmationContent(
                    hasPreAuthorizations = state.hasPreAuthorizations,
                    onBack = onBack,
                    onConfirm = viewModel::onConfirm,
                    onExit = onExit
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
                    onSuccess()
                }
                ProcessTransactionOK()
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
    hasPreAuthorizations: Boolean,
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

    var confirmSelection by remember { mutableStateOf(false) }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
    }

    if (confirmSelection && hasPreAuthorizations) {
        Alert(
            onDismissDialog = { confirmSelection = false },
            onConfirmDialog = onConfirm,
            title = stringResource(R.string.you_have_pending_authorizations),
            subtitle = stringResource(R.string.you_have_pending_authorizations_if_you_continue_they_will_be_completed_with_zero_amount_do_you_wish_to_proceed)
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
        InfoScreenTemplate(
            modifier = Modifier.padding(paddingValues),
            title = stringResource(R.string.do_you_want_to_do_a_batch_close),
            imageRes = AATIcons.batch,
            imageSize = 230.dp,
            description = "",
            buttonText = stringResource(R.string.yes),
            exitButton = stringResource(R.string.cancel),
            onConfirmClick = {
                if (hasPreAuthorizations) {
                    confirmSelection = true
                } else {
                    onConfirm()
                }
            },
            onCancelClick = {
                cancelSelection = true
            },
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
private fun ProcessTransactionOK() {
    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        ProcessOkScreen(
            text = stringResource(R.string.batch_closed),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {}
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
                    description = stringResource(R.string.communication_error_with_controller),
                    auxiliaryText = "",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}