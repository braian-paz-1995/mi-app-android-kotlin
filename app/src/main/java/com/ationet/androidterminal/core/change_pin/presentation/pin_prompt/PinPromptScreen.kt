package com.ationet.androidterminal.core.change_pin.presentation.pin_prompt

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKDescription
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkAuthorizationCode
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.SupervisorPasswordScreen
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun PinPromptScreen(
    viewModel: PinPromptViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExit: () -> Unit,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
    }

    when (val navigationState = viewModel.navigationState.collectAsStateWithLifecycle().value) {
        PinPromptEntryState.CurrentPin -> {
            CurrentPin(
                password = state.currentPin,
                passwordError = state.currentPinError?.message?.let { stringResource(it) } ?: "",
                onPasswordChange = {
                    val filteredInput = it.filter { char ->
                        char.isDigit()
                    }
                    viewModel.onAction(PinPromptAction.OnCurrentPinChange(filteredInput))
                },
                onToggleVisibility = {
                    viewModel.onAction(PinPromptAction.OnToggleVisibilityCurrentPin)
                },
                visibilityPassword = state.currentPinVisibility,
                onClickNext = {
                    viewModel.onAction(PinPromptAction.OnNext)
                },
                onClickBack = onBack,
                onExit = { cancelSelection = true }
            )
        }

        PinPromptEntryState.ConfirmPin -> {
            ConfirmationPin(
                password = state.confirmPin,
                passwordError = state.confirmPinError?.message?.let { stringResource(it) } ?: "",
                onPasswordChange = {
                    val filteredInput = it.filter { char ->
                        char.isDigit()
                    }
                    viewModel.onAction(PinPromptAction.OnConfirmPinChange(filteredInput))
                },
                onToggleVisibility = {
                    viewModel.onAction(PinPromptAction.OnToggleVisibilityConfirmPin)
                },
                visibilityPassword = state.confirmPinVisibility,
                onClickNext = {
                    viewModel.onAction(PinPromptAction.OnNext)
                },
                onClickBack = {
                    viewModel.onAction(PinPromptAction.OnBack)
                },
                onExit = { cancelSelection = true }
            )
        }

        PinPromptEntryState.ReConfirmPin -> {
            ReConfirmationPin(
                password = state.reConfirmPin,
                passwordError = state.reConfirmPinError?.message?.let { stringResource(it) } ?: "",
                onPasswordChange = {
                    val filteredInput = it.filter { char ->
                        char.isDigit()
                    }
                    viewModel.onAction(PinPromptAction.OnReConfirmPinChange(filteredInput))
                },
                onToggleVisibility = {
                    viewModel.onAction(PinPromptAction.OnToggleVisibilityReConfirmPin)
                },
                visibilityPassword = state.reConfirmPinVisibility,
                onClickNext = {
                    viewModel.onAction(PinPromptAction.OnSubmit)
                },
                onClickBack = {
                    viewModel.onAction(PinPromptAction.OnBack)
                },
                onExit = { cancelSelection = true }
            )
        }

        PinPromptEntryState.Loading -> {
            state.loadingState?.let {
                LoadingScreen(
                    loadingState = it,
                    onSuccess = { },
                    onFailure = { }
                )
            }
        }

        is PinPromptEntryState.TransactionProcessError -> {
            LaunchedEffect(true) {
                delay(2.seconds)
                onFailure()
            }
            ProcessTransactionNOK(
                messageError = navigationState.message
            )
        }

        is PinPromptEntryState.TransactionProcessOk -> {
            LaunchedEffect(true) {
                delay(2.seconds)
                onSuccess()
            }
            ProcessTransactionOK(
                authorizationCode = navigationState.authorizationCode
            )
        }

        PinPromptEntryState.CommunicationError -> {
            LaunchedEffect(Unit) {
                delay(3.seconds)
                onExit()
            }

            CommunicationErrorScreen(onExit)
        }
    }
}

@Composable
private fun CurrentPin(
    password: String,
    passwordError: String?,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    visibilityPassword: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { onClickBack() }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                onBack = onClickBack,
                shouldDisplayLogoIcon = true,
            )
        }
    ) { innerPadding ->
        SupervisorPasswordScreen(
            title = stringResource(R.string.enter_your_current_pin),
            modifier = Modifier.padding(innerPadding),
            password = password,
            label = stringResource(R.string.pin),
            passwordError = passwordError.orEmpty(),
            onPasswordChange = onPasswordChange,
            onToggleVisibility = onToggleVisibility,
            visibilityPassword = visibilityPassword,
            onClickNext = onClickNext,
            onExit = onExit,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                showKeyboardOnFocus = true
            )
        )
    }
}

@Composable
private fun ConfirmationPin(
    password: String,
    passwordError: String?,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    visibilityPassword: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { onClickBack() }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                onBack = onClickBack,
                shouldDisplayLogoIcon = true,
            )
        }
    ) { innerPadding ->
        SupervisorPasswordScreen(
            title = stringResource(R.string.enter_your_new_pin),
            modifier = Modifier.padding(innerPadding),
            password = password,
            label = stringResource(R.string.pin),
            passwordError = passwordError.orEmpty(),
            onPasswordChange = onPasswordChange,
            onToggleVisibility = onToggleVisibility,
            visibilityPassword = visibilityPassword,
            onClickNext = onClickNext,
            onExit = onExit,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                showKeyboardOnFocus = true
            )
        )
    }
}

@Composable
private fun ReConfirmationPin(
    password: String,
    passwordError: String?,
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    visibilityPassword: Boolean,
    onClickNext: () -> Unit,
    onClickBack: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { onClickBack() }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                onBack = onClickBack,
                shouldDisplayLogoIcon = true,
            )
        }
    ) { innerPadding ->
        SupervisorPasswordScreen(
            title = stringResource(R.string.confirm_your_new_pin),
            modifier = Modifier.padding(innerPadding),
            password = password,
            label = stringResource(R.string.pin),
            passwordError = passwordError.orEmpty(),
            onPasswordChange = onPasswordChange,
            onToggleVisibility = onToggleVisibility,
            visibilityPassword = visibilityPassword,
            onClickNext = onClickNext,
            onExit = onExit,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                showKeyboardOnFocus = true
            )
        )
    }
}

@Composable
private fun ProcessTransactionNOK(
    messageError: String
) {
    BackHandler { }

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
    BackHandler { }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        ProcessOkScreen(
            text = stringResource(R.string.your_pin_has_been_changed),
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
                    description = stringResource(R.string.communication_error_with_controller),
                    auxiliaryText = "",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}