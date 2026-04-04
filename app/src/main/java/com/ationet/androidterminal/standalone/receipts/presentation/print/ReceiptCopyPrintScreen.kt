package com.ationet.androidterminal.standalone.receipts.presentation.print

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
import com.ationet.androidterminal.core.presentation.PrintingDoneScreen
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons

@Composable
fun ReceiptCopyScreen(
    onExit: () -> Unit,
    viewModel: ReceiptCopyPrintViewModel = hiltViewModel()
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        ReceiptPrintState.PrintingCopy -> ReceiptPrintingCopyScreen()
        ReceiptPrintState.PrintingCopyDone -> ReceiptPrintingCopyDoneScreen(
            onExit = onExit
        )
        is ReceiptPrintState.PrintingError -> ReceiptPrintingErrorScreen(
            onExit = onExit,
            onRetry = viewModel::retryPrinting,
            errorCode = state.errorCode
        )
        ReceiptPrintState.PrintingOutOfPaper -> ReceiptPrinterOutOfPaperScreen(
            onExit = onExit,
            onRetry = viewModel::retryPrinting
        )
    }
}

@Composable
private fun ReceiptPrintingCopyScreen() {
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
private fun ReceiptPrintingCopyDoneScreen(
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
private fun ReceiptPrintingErrorScreen(
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
private fun ReceiptPrinterOutOfPaperScreen(
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