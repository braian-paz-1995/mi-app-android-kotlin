package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.TextFieldOutlined
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun SupervisorPasswordScreen(
    title: String,
    password: String,
    passwordError: String? = "",
    label: String = stringResource(R.string.password),
    onPasswordChange: (String) -> Unit,
    onToggleVisibility: () -> Unit,
    visibilityPassword: Boolean,
    onClickNext: () -> Unit,
    onExit: (() -> Unit)? = null,
    modifier: Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val focusRequester = remember { FocusRequester() }

    val icon = if (visibilityPassword) R.drawable.eye_close else R.drawable.eye_open

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AATHeaderTitle(title)

        TextFieldOutlined(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = password,
            onValueChange = { newText ->
                onPasswordChange(newText)
            },
            label = label,
            visualTransformation = if (visibilityPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation('*')
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { onToggleVisibility() }
                )
            },
            isError = !passwordError.isNullOrEmpty(),
            supportingText = passwordError,
            keyboardOptions = keyboardOptions
        )

        AATButton(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.primary,
            onClick = onClickNext,
            enabled = password.isNotEmpty()
        ) {
            Text(stringResource(R.string.next))
        }

        if (onExit != null) {
            AATTextButton(
                text = stringResource(R.string.exit),
                onClick = onExit,
                textColor = MaterialTheme.colorScheme.error
            )
        }
    }
}


@NewlandPreview
@Composable
private fun PreviewEnterNewSupervisorPasswordScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader(
                    navigationIcon = {
                        AATTextButton(
                            text = "Go back",
                            onClick = {},
                            modifier = Modifier.align(Alignment.Start),
                            textColor = MaterialTheme.colorScheme.secondary,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            SupervisorPasswordScreen(
                modifier = Modifier
                    .padding(innerPadding),
                title = "Enter new supervisor's password",
                password = "",
                passwordError = "",
                onPasswordChange = {},
                onToggleVisibility = {},
                visibilityPassword = true,
                onClickNext = {}
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewConfirmSupervisorPasswordScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader(
                    navigationIcon = {
                        AATTextButton(
                            text = "Go back",
                            onClick = {},
                            modifier = Modifier.align(Alignment.Start),
                            textColor = MaterialTheme.colorScheme.secondary,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            SupervisorPasswordScreen(
                modifier = Modifier
                    .padding(innerPadding),
                title = "Confirm supervisor's password",
                password = "",
                passwordError = "",
                onPasswordChange = {},
                onToggleVisibility = {},
                visibilityPassword = true,
                onClickNext = {}
            )
        }
    }
}