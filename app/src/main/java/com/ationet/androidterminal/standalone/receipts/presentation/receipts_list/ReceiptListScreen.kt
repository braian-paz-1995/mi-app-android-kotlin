package com.ationet.androidterminal.standalone.receipts.presentation.receipts_list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.components.AATInfoCard
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.receipts.ReceiptsScreen
import com.ationet.androidterminal.ui.theme.AATIcons

@Composable
fun ReceiptListScreen(
    onExit: () -> Unit,
    onReceiptSelected: (Int) -> Unit,
    viewModel: ReceiptListViewModel = hiltViewModel(),
) {
    val items = viewModel.receipts.collectAsLazyPagingItems()
    val refreshState = items.loadState.refresh

    /* Handle loading */
    if (refreshState is LoadState.Loading) {
        BackHandler {  }

        LoadingScreen(
            loadingState = LoadingState.Loading,
            onSuccess = {},
            onFailure = {}
        )
    } else {
        val snackBarHostState = remember { SnackbarHostState() }
        val errorLoading = stringResource(R.string.error_loading_receipts)

        BackHandler { onExit.invoke() }
        // If error display error
        if(refreshState is LoadState.Error) {
            LaunchedEffect(Unit) {
                snackBarHostState.showSnackbar(errorLoading)
            }
        }

        AATScaffold(
            shouldDisplayNavigationIcon = true,
            onBack = onExit,
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { paddingValues ->
            if(refreshState is LoadState.NotLoading && items.itemCount > 0) {
                // Display items
                ReceiptsScreen(
                    transactions = items,
                    onReceiptClick = onReceiptSelected,
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                ReceiptsEmptyScreen(
                    modifier = Modifier.padding(paddingValues),
                    onExit = onExit
                )
            }
        }
    }
}

@Composable
private fun ReceiptsEmptyScreen(
    modifier: Modifier,
    onExit: () -> Unit,
) {
    InfoScreenTemplate(
        modifier = modifier,
        title = stringResource(R.string.no_receipts_found),
        imageRes = AATIcons.empty,
        imageSize = 170.dp,
        description = stringResource(R.string.no_receipts_to_display),
        buttonText = stringResource(R.string.okay_exit),
        onConfirmClick = onExit,
        onCancelClick = {},
        extraContent = {
            AATInfoCard(
                text = stringResource(R.string.no_receipts_found_text)
            )
        }
    )
}
