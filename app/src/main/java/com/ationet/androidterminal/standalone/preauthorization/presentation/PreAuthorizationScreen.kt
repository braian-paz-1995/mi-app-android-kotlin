package com.ationet.androidterminal.standalone.preauthorization.presentation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.clear_pending.presentation.ClearPendingTransactionDestination
import com.ationet.androidterminal.standalone.clear_pending.presentation.NavOriginClearPending
import com.ationet.androidterminal.standalone.completion.navigation.CompletionDestination
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import com.ationet.androidterminal.standalone.preauthorization.presentation.confirmation.ConfirmationScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.identification.IdentificationViewModel
import com.ationet.androidterminal.standalone.preauthorization.presentation.pending_transaction.PendingTransactionScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.product_option.ProductOptionScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.product_selection.ProductSelectionScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.transaction_amount.TransactionAmountScreen
import com.ationet.androidterminal.standalone.preauthorization.presentation.transaction_process.TransactionProcessScreen

fun NavGraphBuilder.preAuthorizationScreen(navController: NavController) {
    composable<PreAuthorizationDestination.Identification> {
        val viewModel = hiltViewModel<IdentificationViewModel>()

        /* We set it here since the viewmodel is reused in the prompts and with this we avoid restarting it. */
        LaunchedEffect(Unit) {
            viewModel.setNewStateOperation()
        }

        IdentificationScreen(
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onIdentificationPresented = {
                viewModel.setIdentification(it)
            },
            navController = navController
        )
    }

    composable<PreAuthorizationDestination.PendingTransaction> {
        PendingTransactionScreen(
            onComplete = {
                navController.navigateAndPopUp(
                    CompletionDestination.TransactionToCompleteConfirmation(
                        identification = it
                    )
                )
            },
            onClear = {
                navController.navigateAndPopUp(ClearPendingTransactionDestination.TransactionConfirmation(NavOriginClearPending.PreAuthorization, it))
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onProductNavigation = {
                navController.navigateAndPopUp(PreAuthorizationDestination.ProductSelection)
            }
        )
    }

    composable<PreAuthorizationDestination.ProductSelection> {
        ProductSelectionScreen(
            onBack = {
                navController.navigateAndPopUp(PreAuthorizationDestination.Identification)
            },
            onProductSelected = {
                navController.navigateAndPopUp(PreAuthorizationDestination.ProductOption)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<PreAuthorizationDestination.ProductOption> {
        ProductOptionScreen(
            onQuantityAmountSelected = {
                navController.navigateAndPopUp(PreAuthorizationDestination.TransactionAmount)
            },
            onFillUpSelected = {
                navController.navigateAndPopUp(PreAuthorizationDestination.TransactionConfirmation)
            },
            onBack = {
                navController.navigateAndPopUp(PreAuthorizationDestination.ProductSelection)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<PreAuthorizationDestination.TransactionAmount> {
        TransactionAmountScreen(
            onBack = {
                navController.navigateAndPopUp(PreAuthorizationDestination.ProductOption)
            },
            onTransactionCompleted = {
                navController.navigateAndPopUp(PreAuthorizationDestination.TransactionConfirmation)
            }
        )
    }

    composable<PreAuthorizationDestination.TransactionConfirmation> {
        ConfirmationScreen(
            onBack = {
                navController.navigateAndPopUp(PreAuthorizationDestination.ProductOption)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onConfirmed = {
                navController.navigateAndPopUp(PreAuthorizationDestination.TransactionProcess)
            }
        )
    }

    composable<PreAuthorizationDestination.TransactionProcess> {
        TransactionProcessScreen(
            onSuccess = {
                navController.navigateAndPopUp(PreAuthorizationDestination.PreAuthorizationSummary)
            },
            onFailure = {
                navController.navigateAndPopUp(PreAuthorizationDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            navController = navController
        )
    }

    composable<PreAuthorizationDestination.PreAuthorizationSummary> {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(PreAuthorizationDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<PreAuthorizationDestination.Print> {
        PrintScreen(
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
}