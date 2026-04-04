package com.ationet.androidterminal.standalone.sale.presentation.transaction_process

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.MorePromptsScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKDescription
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkAuthorizationCode
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.presentation.components.GenericSingleLineTextField
import com.ationet.androidterminal.core.presentation.components.TextFieldWithCounter
import com.ationet.androidterminal.core.presentation.util.handleDecimalTextFieldChange
import com.ationet.androidterminal.standalone.preauthorization.domain.model.Prompt
import com.ationet.androidterminal.standalone.sale.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.standalone.sale.presentation.identification.IdentificationViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@Composable
fun TransactionProcessScreen(
    viewModel: TransactionProcessViewModel = hiltViewModel(),
    onSuccess: () -> Unit,
    onFailure: () -> Unit,
    navController: NavController,
    onExit: () -> Unit
) {
    var cancelSelection by remember {
        mutableStateOf(false)
    }
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is TransactionProcessState.LoadingTransaction -> {
            LoadingScreen(
                loadingState = state.loadingState,
                onSuccess = { },
                onFailure = { }
            )
        }

        is TransactionProcessState.RequiredPrompts -> {
            PromptScreen(
                promptType = state.prompt.type,
                promptTitleId = state.promptTitle.title,
                promptLength = state.promptTitle.maxLength,
                promptKey = state.prompt.key,
                onContinue = {
                    viewModel.setPromptValue(state.prompt.key, it)
                },
                navController = navController,
                onExit = {
                    cancelSelection = true
                }
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

        TransactionProcessState.LookingForPrompts -> {
            MorePromptsScreen(modifier = Modifier)
        }

        TransactionProcessState.CommunicationError -> {
            LaunchedEffect(Unit) {
                delay(3.seconds)
                onFailure()
            }

            CommunicationErrorScreen(
                onExit = onFailure
            )
        }
    }

}

@Composable
private fun PromptScreen(
    promptType: Prompt.PromptType,
    promptTitleId: Int,
    promptLength: Int,
    promptKey: String?,
    onContinue: (String) -> Unit,
    navController: NavController,
    onExit: () -> Unit,
    viewModel: TransactionProcessViewModel = hiltViewModel(),
) {
    BackHandler { }

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
    }

    when (promptType) {
        Prompt.PromptType.Identifier -> {
            PromptIdentifier(
                onExit = onExit,
                onContinue = onContinue,
                titleId = promptTitleId
            )
        }
        Prompt.PromptType.Attendant -> {
            PromptIdentifier(
                onExit = onExit,
                onContinue = onContinue,
                titleId = promptTitleId
            )
        }
        Prompt.PromptType.Pin -> {
            AATScaffold(
                topBar = {
                    AATTopBar(
                        shouldDisplayNavigationIcon = false,
                        shouldDisplayLogoIcon = true
                    )
                }
            ) { innerPadding ->
                PromptPin(
                    modifier = Modifier.padding(innerPadding),
                    title = stringResource(R.string.enter_pin),
                    onContinue = onContinue,
                    onCancel = { cancelSelection = true }
                )
            }

        }
        Prompt.PromptType.VisionRecognition -> {
            com.ationet.androidterminal.core.presentation.PromptPhoto(
                onExit = onExit,
                titleId = promptTitleId,
                onContinue = { value ->
                    promptKey?.let { key ->
                        viewModel.setPromptValue(key, value)
                    }
                }
            )
        }

        Prompt.PromptType.Alphanumeric -> {
            AATScaffold(
                topBar = {
                    AATTopBar(
                        shouldDisplayNavigationIcon = false,
                        shouldDisplayLogoIcon = true
                    )
                }
            ) { innerPadding ->
                PromptAlphanumeric(
                    modifier = Modifier.padding(innerPadding),
                    title = "${stringResource(R.string.enter)} ${
                        stringResource(promptTitleId).lowercase(
                            Locale.getDefault()
                        )
                    }",
                    onContinue = onContinue,
                    onCancel = { cancelSelection = true },
                    maxLength = promptLength
                )
            }
        }

        Prompt.PromptType.Numeric -> {
            AATScaffold(
                topBar = {
                    AATTopBar(
                        shouldDisplayNavigationIcon = false,
                        shouldDisplayLogoIcon = true
                    )
                }
            ) { innerPadding ->
                PromptNumeric(
                    modifier = Modifier.padding(innerPadding),
                    title = "${stringResource(R.string.enter)} ${
                        stringResource(promptTitleId).lowercase(
                            Locale.getDefault()
                        )
                    }",
                    onContinue = onContinue,
                    onCancel = { cancelSelection = true },
                    maxLength = promptLength
                )
            }
        }
    }
}

@Composable
private fun PromptIdentifier(
    @StringRes titleId: Int,
    onExit: () -> Unit,
    onContinue: (String) -> Unit
) {
    val viewModel = hiltViewModel<IdentificationViewModel>()
    IdentificationScreen(
        titleId = titleId,
        viewModel = viewModel,
        onExit = onExit,
        onIdentificationPresented = {
            viewModel.playBeep()
            onContinue(it)
            viewModel.resetState()
        },
        showBackButton = false,
        showCancelButton = true
    )
}

@Composable
private fun PromptPin(
    modifier: Modifier = Modifier,
    title: String,
    onContinue: (String) -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember { mutableStateOf("") }
    var visibilityPassword by remember { mutableStateOf(false) }
    val icon = if (visibilityPassword) R.drawable.eye_close else R.drawable.eye_open
    var errorText by remember { mutableStateOf<String?>(null) }
    val valueEmpty = stringResource(R.string.empty_value)
    val minimumCharacters = stringResource(R.string.enter_at_least_4_characters)

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
                TextFieldWithCounter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = textFieldValue,
                    onValueChange = OnValueChanged@{
                        if (it == textFieldValue) {
                            textFieldValue = it
                            return@OnValueChanged
                        }

                        textFieldValue = it

                        val value = textFieldValue

                        errorText = if (value == "") {
                            valueEmpty
                        } else {
                            null
                        }
                    },
                    label = stringResource(R.string.pin),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
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
                                .clickable {
                                    visibilityPassword = !visibilityPassword
                                }
                        )
                    },
                    isError = errorText != null,
                    maxLength = 8,
                    supportingText = errorText.orEmpty()
                )
                AATButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        errorText = if (textFieldValue.length < 4) {
                            minimumCharacters
                        } else {
                            null
                        }
                        if (errorText == null) {
                            onContinue(textFieldValue)
                            textFieldValue = ""
                        }
                    },
                    enabled = textFieldValue.isNotEmpty()
                ) {
                    Text(stringResource(R.string.next))
                }

                AATTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onCancel,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PromptAlphanumeric(
    modifier: Modifier = Modifier,
    title: String,
    maxLength: Int,
    onContinue: (String) -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorText by remember { mutableStateOf<String?>(null) }
    val valueEmpty = stringResource(R.string.empty_value)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(200)
        keyboardController?.show()
    }

    Surface(modifier = modifier) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AATHeaderTitle(title)
                GenericSingleLineTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    value = textFieldValue,
                    onValueChange = OnValueChanged@{
                        if (it == textFieldValue) {
                            textFieldValue = it
                            return@OnValueChanged
                        }

                        textFieldValue = it

                        val value = textFieldValue

                        errorText = if (value == "") {
                            valueEmpty
                        } else {
                            null
                        }
                    },
                    isError = errorText != null,
                    supportingText = errorText.orEmpty(),
                    maxLength = maxLength
                )
                AATButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        onContinue(textFieldValue)
                        textFieldValue = ""
                    },
                    enabled = textFieldValue.isNotEmpty()
                ) {
                    Text(stringResource(R.string.continue_button))
                }

                AATTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onCancel,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun PromptNumeric(
    modifier: Modifier = Modifier,
    title: String,
    maxLength: Int,
    onContinue: (String) -> Unit,
    onCancel: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var errorText by remember { mutableStateOf<String?>(null) }
    val valueEmpty = stringResource(R.string.empty_value)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(200)
        keyboardController?.show()
    }

    Surface(modifier = modifier) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AATHeaderTitle(title)
                GenericSingleLineTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    value = textFieldValue,
                    onValueChange = OnValueChanged@{
                        if (it == textFieldValue) {
                            textFieldValue = TextFieldValue(it).text
                            return@OnValueChanged
                        }

                        textFieldValue = handleDecimalTextFieldChange(
                            textFieldValue = TextFieldValue(it)
                        ).text

                        val value = textFieldValue.toDoubleOrNull() ?: return@OnValueChanged

                        errorText = if (value == 0.0) {
                            valueEmpty
                        } else {
                            null
                        }
                    },
                    isError = errorText != null,
                    supportingText = errorText.orEmpty(),
                    maxLength = maxLength
                )
                AATButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        onContinue(textFieldValue)
                        textFieldValue = ""
                    },
                    enabled = textFieldValue.isNotEmpty()
                ) {
                    Text(stringResource(R.string.continue_button))
                }
                AATTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onCancel,
                    textColor = MaterialTheme.colorScheme.error
                )
            }

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
