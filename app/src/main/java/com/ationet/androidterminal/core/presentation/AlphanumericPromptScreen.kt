package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.GenericSingleLineTextField
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview
import kotlinx.coroutines.delay

@Composable
fun AlphanumericPromptScreen(
    title: String,
    textFieldValue: TextFieldValue,
    modifier: Modifier = Modifier,
    errorMessage: String = "",
    onTextFieldValueChanged: (TextFieldValue) -> Unit,
    onContinue: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(200)
        keyboardController?.show()
    }

    Surface(modifier = modifier) {
        Box(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AATHeaderTitle(title)
                GenericSingleLineTextField(
                    value = textFieldValue.text,
                    onValueChange = { newValue ->
                        onTextFieldValueChanged(TextFieldValue(newValue))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    isError = errorMessage.isNotEmpty(),
                    supportingText = errorMessage,
                    maxLength = 19
                )
                AATButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = onContinue,
                    enabled = textFieldValue.text.isNotEmpty()
                ) {
                    Text(stringResource(R.string.continue_button))
                }

            }
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewAlphanumericPromptScreen() {
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
            AlphanumericPromptScreen(
                modifier = Modifier
                    .padding(innerPadding),
                textFieldValue = TextFieldValue(""),
                title = "Enter your identification manually",
                onTextFieldValueChanged = { },
                onContinue = { }
            )
        }
    }
}