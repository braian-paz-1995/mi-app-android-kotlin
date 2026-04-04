package com.ationet.androidterminal.standalone.completion.presentation.printing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.PrintingCopyScreen
import com.ationet.androidterminal.core.presentation.PrintingDoneAcceptButton
import com.ationet.androidterminal.core.presentation.PrintingDonePrintCopyButton
import com.ationet.androidterminal.core.presentation.PrintingDoneScreen
import com.ationet.androidterminal.core.presentation.PrintingScreen
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons

@Composable
fun CompletionPrintScreen(
    viewModel: CompletionPrintViewModel = hiltViewModel(),
    onExit: () -> Unit,
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    when (state) {
        CompletionPrintState.Printing -> CompletionPrintingScreen(

        )

        CompletionPrintState.PrintingDone -> CompletionPrintingDoneScreen(
            onExit = onExit,
            onPrintCopy = viewModel::onPrintCopy
        )

        CompletionPrintState.PrintingCopy -> CompletionPrintingCopyScreen(

        )

        CompletionPrintState.PrintingCopyDone -> CompletionPrintingCopyDoneScreen(
            onExit = onExit
        )

        is CompletionPrintState.PrintingError -> {
            if (state.error == CompletionPrintState.PrinterError.OutOfPaper) {
                CompletionPrinterOutOfPaperScreen(
                    onExit = onExit,
                    onRetry = viewModel::onRetry
                )
            } else {
                CompletionPrintingErrorScreen(
                    errorCode = state.errorCode,
                    onExit = onExit,
                    onRetry = viewModel::onRetry
                )
            }
        }
    }
}

@Composable
private fun CompletionPrintingScreen() {
    BackHandler {  }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        PrintingScreen(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CompletionPrintingDoneScreen(
    onExit: () -> Unit,
    onPrintCopy: () -> Unit,
) {
    BackHandler {
        onExit.invoke()
    }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        PrintingDoneScreen(
            modifier = Modifier.padding(paddingValues),
            actions = {
                PrintingDonePrintCopyButton(
                    onClick = onPrintCopy
                )
                PrintingDoneAcceptButton(
                    onClick = onExit
                )
            }
        )
    }
}

@Composable
private fun CompletionPrintingCopyScreen() {
    BackHandler { }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        PrintingCopyScreen(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun CompletionPrintingCopyDoneScreen(
    onExit: () -> Unit,
) {
    BackHandler {
        onExit.invoke()
    }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        PrintingDoneScreen(
            modifier = Modifier.padding(paddingValues),
            actions = {
                PrintingDoneAcceptButton(
                    onClick = onExit
                )
            }
        )
    }
}

@Composable
private fun CompletionPrintingErrorScreen(
    errorCode: String,
    onExit: () -> Unit,
    onRetry: () -> Unit,
) {
    BackHandler {
        onExit.invoke()
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        val errorCodeString = stringResource(R.string.error_code, errorCode)

        InfoScreenTemplate(
            modifier = Modifier.padding(paddingValues),
            title = stringResource(R.string.printer_issue),
            imageRes = AATIcons.printerError,
            imageSize = 200.dp,
            description = stringResource(R.string.printer_issue_message),
            buttonText = stringResource(R.string.try_again),
            exitButton = stringResource(R.string.exit),
            extraContent = {
                Text(
                    text = errorCodeString,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                )
            },
            onConfirmClick = onRetry,
            onCancelClick = onExit
        )
    }
}

@Composable
fun CompletionPrinterOutOfPaperScreen(
    onExit: () -> Unit,
    onRetry: () -> Unit,
) {
    BackHandler {
        onExit.invoke()
    }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->
        InfoScreenTemplate(
            modifier = Modifier.padding(paddingValues),
            title = stringResource(R.string.printer_out_of_paper),
            imageRes = AATIcons.printerError,
            imageSize = 200.dp,
            description = stringResource(R.string.printer_out_of_paper_message),
            buttonText = stringResource(R.string.all_set_try_again),
            onConfirmClick = onRetry,
            onCancelClick = onExit
        )
    }
}