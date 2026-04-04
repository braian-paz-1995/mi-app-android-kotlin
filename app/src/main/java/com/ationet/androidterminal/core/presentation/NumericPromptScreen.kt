package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATToggle
import com.ationet.androidterminal.core.presentation.components.AmountQuantityTextField
import com.ationet.androidterminal.core.presentation.components.GenericSingleLineTextField
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun NumericPromptScreen(
    title: String,
    displayType: DisplayType,
    onDisplayTypeChanged: (DisplayType) -> Unit,
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    errorText: String? = null,
    currency: String,
    fuelMeasureUnit: String,
    showToggle: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }

    NumericInput(
        title = title,
        modifier = modifier,
        header = {
            if (showToggle) {
                AATToggle(
                    checked = displayType,
                    onCheckedChange = onDisplayTypeChanged
                )
            }
        },
        textField = {
            AmountQuantityTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    showKeyboardOnFocus = true,
                    keyboardType = KeyboardType.Number
                ),
                value = value,
                onValueChange = onValueChanged,
                displayType = displayType,
                quantityOrAmount = if (displayType == DisplayType.QUANTITY) {
                    fuelMeasureUnit
                } else {
                    currency
                },
                isError = !errorText.isNullOrEmpty(),
                supportingText = errorText.orEmpty()
            )
        },
        buttons = {
            AATButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.primary,
                onClick = onContinue,
                enabled = value.text.isNotEmpty() && errorText.isNullOrEmpty()
            ) {
                Text(stringResource(R.string.continue_button))
            }
        }
    )
}

@Composable
fun NumericPromptScreen(
    title: String,
    value: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    maxLength: Int? = null,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        showKeyboardOnFocus = true,
        keyboardType = KeyboardType.Number
    ),
    buttons: @Composable (ColumnScope.() -> Unit)? = null,
    onValueChanged: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(200)
    }

    NumericInput(
        title = title,
        modifier = modifier,
        header = null,
        buttons = buttons,
        textField = {
            GenericSingleLineTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = value,
                onValueChange = onValueChanged,
                isError = isError,
                supportingText = supportingText,
                keyboardOptions = keyboardOptions,
                maxLength = maxLength
            )
        }
    )
}

@Composable
private fun NumericInput(
    title: String,
    modifier: Modifier = Modifier,
    textField: @Composable () -> Unit,
    header: @Composable (() -> Unit)?,
    buttons: @Composable (ColumnScope.() -> Unit)?,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        AATHeaderTitle(title)

        header?.invoke()

        textField.invoke()

        buttons?.invoke(this)
    }
}

@NewlandPreview
@Composable
private fun PreviewNumericPromptScreen() {
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
            NumericPromptScreen(
                modifier = Modifier
                    .padding(innerPadding),
                title = "Enter value",
                value = "",
                onValueChanged = { },
                isError = false,
                supportingText = null,
                buttons = {
                    AATButton(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = { },
                        enabled = true
                    ) {
                        Text(stringResource(R.string.continue_button))
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun Preview() {
    AtionetAndroidTerminalTheme {
        AATScaffold(
            topBar = {
                AATHeader(modifier = Modifier.background(Color.White))
            }
        ) { paddingValues ->
            NumericPromptScreen(
                modifier = Modifier
                    .padding(paddingValues),
                title = "Enter value",
                displayType = DisplayType.AMOUNT,
                value = TextFieldValue(""),
                onValueChanged = { },
                onDisplayTypeChanged = { },
                onContinue = { },
                currency = "$",
                fuelMeasureUnit = "L"
            )
        }
    }
}