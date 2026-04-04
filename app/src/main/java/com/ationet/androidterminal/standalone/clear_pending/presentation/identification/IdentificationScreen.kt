package com.ationet.androidterminal.standalone.clear_pending.presentation.identification

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.IdentificationErrorScreen
import com.ationet.androidterminal.core.presentation.IdentificationReadInProgressScreen
import com.ationet.androidterminal.core.presentation.IdentificationReaderScreen
import com.ationet.androidterminal.core.presentation.IdentificationScanScreen
import com.ationet.androidterminal.core.presentation.IdentificationWaitingToRemoveScreen
import com.ationet.androidterminal.core.presentation.ManualEntryScreen
import com.ationet.androidterminal.core.presentation.QRErrorScreen
import com.ationet.androidterminal.core.presentation.TimeoutScreen
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.clear_pending.presentation.ClearPendingTransactionDestination
import com.ationet.androidterminal.standalone.clear_pending.presentation.NavOriginClearPending

@Composable
fun IdentificationScreen(
    viewModel: IdentificationViewModel = hiltViewModel(),
    onExit: () -> Unit,
    onIdentificationPresented: (String) -> Unit,
    navController: NavController = rememberNavController(),
    showBackButton: Boolean = true,
    showCancelButton: Boolean = false,
    @StringRes titleId: Int = R.string.confirmation_identification_title
) {

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = {
                cancelSelection = false
                onExit()
            }
        )
    }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        IdentificationEntryState.SwipeInsertScanIdentification -> {
            IdentificationReaderScreen(
                titleId = titleId,
                onBack = onExit,
                onScanClicked = viewModel::requestCameraScan,
                onManualEntryClicked = viewModel::requestManualEntry,
                showBackButton = showBackButton,
                showCancelButton = showCancelButton
            )
        }

        is IdentificationEntryState.SwipeInsertScanInProgress -> {
            IdentificationReadInProgressScreen(
                type = state.identificationType
            )
        }

        is IdentificationEntryState.SwipeInsertScanDone -> {
            IdentificationWaitingToRemoveScreen(
                type = state.identificationType
            )
        }

        is IdentificationEntryState.ScanCompleted -> {
            LaunchedEffect(Unit) {
                onIdentificationPresented(state.identifier)
            }

            /* Yes, I'm using the same screen to avoid white screen on callback */
            IdentificationWaitingToRemoveScreen(
                type = state.identificationType
            )
        }

        IdentificationEntryState.SwipeInsertScanError -> {
            IdentificationErrorScreen(
                onRetry = viewModel::retryCardRead,
                onExit = {
                    cancelSelection = true
                }
            )
        }

        IdentificationEntryState.CameraScanIdentification -> {
            IdentificationScanScreen(
                onBack = viewModel::retryCardRead,
                onIdentificationScanComplete = {
                    onIdentificationPresented(it)
                }
            )
        }

        IdentificationEntryState.ManualIdentification -> {
            ManualEntryScreen(
                onBack = viewModel::retryCardRead,
                onAcceptEntry = {
                    onIdentificationPresented(it)
                }
            )
        }

        IdentificationEntryState.SwipeInsertScanTimeout -> {
            TimeoutScreen(
                onRetry = viewModel::retryCardRead,
                onExit = {
                    cancelSelection = true
                }
            )
        }

        is IdentificationEntryState.SwipeInsertScanEmpty -> {
            if (state.isQrScan) {
                QRErrorScreen(
                    onRetry = viewModel::retryCardRead,
                    onExit = onExit
                )
            } else {
                IdentificationErrorScreen(
                    onRetry = viewModel::retryCardRead,
                    onExit = onExit
                )
            }
        }

        is IdentificationEntryState.IdentifierPresented -> {
            navController.navigateAndPopUp(
                ClearPendingTransactionDestination.TransactionConfirmation(NavOriginClearPending.ClearPending)
            )
        }
    }
}