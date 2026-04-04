package com.ationet.androidterminal.standalone.preauthorization.presentation.pending_transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons
import kotlinx.coroutines.delay

@Composable
fun PendingTransactionScreen(
    viewModel: PendingTransactionViewModel = hiltViewModel(),
    onComplete: (String) -> Unit,
    onClear: (String) -> Unit,
    onExit: () -> Unit,
    onProductNavigation: () -> Unit,
) {
    BackHandler { }

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
            is PendingTransactionState.PendingTransaction -> {
                PendingTransactionContent(
                    onComplete = { onComplete(state.identification) },
                    onClearTransaction = { onClear(state.identification) },
                    onExit = onExit
                )
            }
            PendingTransactionState.ProductSelection -> {
                onProductNavigation()
            }
        }
    }

}


@Composable
fun PendingTransactionContent(
    onComplete: () -> Unit,
    onClearTransaction: () -> Unit,
    onExit: () -> Unit
) {
    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { innerPadding ->
        InfoScreenTemplate(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(R.string.pending_transaction),
            imageRes = AATIcons.clock,
            imageSize = 230.dp,
            description = stringResource(R.string.pending_transaction_description),
            buttonText = stringResource(R.string.clear_transaction),
            exitButton = stringResource(R.string.cancel),
            onConfirmClick = onClearTransaction,
            onCancelClick = onExit,
            extraContent = {
                AATButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    onClick = onComplete,
                    backgroundColor = MaterialTheme.colorScheme.secondary
                ) { Text(stringResource(R.string.let_s_complete_it), fontSize = 16.sp) }
            }
        )
    }
}