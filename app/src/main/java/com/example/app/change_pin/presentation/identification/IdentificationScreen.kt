package com.ationet.androidterminal.core.change_pin.presentation.identification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.core.presentation.IdentificationErrorScreen
import com.ationet.androidterminal.core.presentation.IdentificationReadInProgressScreen
import com.ationet.androidterminal.core.presentation.IdentificationReaderScreen
import com.ationet.androidterminal.core.presentation.IdentificationScanScreen
import com.ationet.androidterminal.core.presentation.IdentificationWaitingToRemoveScreen
import com.ationet.androidterminal.core.presentation.ManualEntryScreen
import com.ationet.androidterminal.core.presentation.QRErrorScreen
import com.ationet.androidterminal.core.presentation.TimeoutScreen
import com.ationet.androidterminal.core.presentation.components.Alert

@Composable
fun IdentificationScreen(
    viewModel: IdentificationViewModel = hiltViewModel(),
    onExit: () -> Unit,
    onIdentificationPresent: (String) -> Unit,
    onNextScreen: (String) -> Unit
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        IdentificationEntryState.SwipeInsertScanIdentification -> {
            IdentificationReaderScreen(
                onBack = onExit,
                onScanClicked = viewModel::requestCameraScan,
                onManualEntryClicked = viewModel::requestManualEntry
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
                viewModel.setIdentification(state.identifier)
                onIdentificationPresent(state.identifier)
            }

            IdentificationWaitingToRemoveScreen(
                type = state.identificationType
            )
        }

        IdentificationEntryState.SwipeInsertScanError -> {
            var cancelRead by remember {
                mutableStateOf(false)
            }

            if (cancelRead) {
                Alert(
                    onDismissDialog = { cancelRead = false },
                    onConfirmDialog = onExit
                )
            }

            IdentificationErrorScreen(
                onRetry = viewModel::retryCardRead,
                onExit = { cancelRead = true }
            )
        }

        IdentificationEntryState.CameraScanIdentification -> {
            IdentificationScanScreen(
                onBack = viewModel::retryCardRead,
                onIdentificationScanComplete = {
                    viewModel.setIdentification(it)
                    onIdentificationPresent(it)
                }
            )
        }

        IdentificationEntryState.ManualIdentification -> {
            ManualEntryScreen(
                onBack = viewModel::retryCardRead,
                onAcceptEntry = {
                    viewModel.setIdentification(it)
                    onIdentificationPresent(it)
                }
            )
        }


        IdentificationEntryState.SwipeInsertScanTimeout -> {
            var cancelRead by remember {
                mutableStateOf(false)
            }

            if (cancelRead) {
                Alert(
                    onDismissDialog = { cancelRead = false },
                    onConfirmDialog = onExit
                )
            }

            TimeoutScreen(
                onRetry = viewModel::retryCardRead,
                onExit = { cancelRead = true }
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
            state.identifier?.let { onNextScreen(it) }
        }
    }
}