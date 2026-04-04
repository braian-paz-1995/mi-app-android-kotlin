package com.ationet.androidterminal.standalone.void_transaction.presentation.authorization_code

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.GenericSingleLineTextField
import kotlinx.coroutines.delay

@Composable
fun EnterAuthorizationCodeScreen(
    viewModel: EnterAuthorizationCodeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onAuthorizationCodeEntered: (authorizationCode: String, transactionType: String) -> Unit
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
            is EnterAuthorizationCodeState.EnterAuthorizationCode -> {
                EnterAuthorizationCodeContent(
                    authorizationCodeError = state.authorizationCodeError,
                    onContinue = { viewModel.onAuthorizationCodeEntered(it) },
                    onBack = onBack
                )
            }

            is EnterAuthorizationCodeState.EnterAuthorizationCodeOk -> onAuthorizationCodeEntered(
                state.authorizationCode,
                state.transactionType
            )

        }
    }
}

@Composable
private fun EnterAuthorizationCodeContent(
    authorizationCodeError: AuthorizationCodeError?,
    onContinue: (String) -> Unit,
    onBack: () -> Unit
) {

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                shouldDisplayLogoIcon = true,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        val focusRequester = remember { FocusRequester() }
        var textFieldValue by remember { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        var resetAuthorizationCodeError by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
            delay(200)
            keyboardController?.show()
        }

        Surface(modifier = Modifier.padding(innerPadding)) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AATHeaderTitle(
                        title = stringResource(R.string.enter_the_authorization_code)
                    )

                    GenericSingleLineTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = textFieldValue,
                        onValueChange = {
                            // Filter out unwanted characters that come with Android's keyboard
                            val filteredInput = it.filter { char ->
                                char.isDigit()
                            }
                            resetAuthorizationCodeError = true
                            textFieldValue = filteredInput
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = !resetAuthorizationCodeError && authorizationCodeError != null,
                        supportingText = if (resetAuthorizationCodeError) "" else authorizationCodeError?.message?.let {
                            stringResource(it)
                        }.orEmpty(),
                        maxLength = 9
                    )

                    AATButton(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            resetAuthorizationCodeError = false
                            onContinue(textFieldValue)
                        },
                        enabled = textFieldValue.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.continue_button))
                    }
                }
            }
        }
    }
}

