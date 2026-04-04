package com.ationet.androidterminal.standalone.batch_close.presentation.supervisor_password

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import com.ationet.androidterminal.core.presentation.SupervisorPasswordScreen
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar

@Composable
fun EnterSupervisorPasswordScreen(
    viewModel: EnterSupervisorPasswordViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSupervisorPasswordEntered: () -> Unit
) {
    BackHandler { onBack() }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is EnterSupervisorPasswordState.EnterSupervisorPassword -> {
            EnterSupervisorPassword(
                supervisorPasswordError = state.supervisorPasswordError,
                onBack = onBack,
                onSupervisorPasswordEntered = { viewModel.onSupervisorPasswordEntered(it) }
            )
        }

        EnterSupervisorPasswordState.EnterSupervisorPasswordOk -> onSupervisorPasswordEntered()
    }
}

@Composable
fun EnterSupervisorPassword(
    supervisorPasswordError: SupervisorPasswordError?,
    onBack: () -> Unit,
    onSupervisorPasswordEntered: (String) -> Unit
) {
    var passwordTextField by remember { mutableStateOf(TextFieldValue()) }
    var visibilityPassword by remember { mutableStateOf(false) }
    var resetPasswordError by remember { mutableStateOf(false) }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                shouldDisplayLogoIcon = true,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        SupervisorPasswordScreen(
            title = stringResource(R.string.enter_supervisor_password_to_continue),
            password = passwordTextField.text,
            passwordError = if (resetPasswordError) "" else supervisorPasswordError?.message?.let {
                stringResource(it)
            }.orEmpty(),
            onPasswordChange = {
                passwordTextField = passwordTextField.copy(text = it)
                resetPasswordError = true
            },
            onToggleVisibility = { visibilityPassword = !visibilityPassword },
            visibilityPassword = visibilityPassword,
            onClickNext = {
                resetPasswordError = false
                onSupervisorPasswordEntered(passwordTextField.text)
            },
            modifier = Modifier.padding(innerPadding),
            keyboardOptions = KeyboardOptions.Default.copy(
                showKeyboardOnFocus = true
            )
        )
    }
}
