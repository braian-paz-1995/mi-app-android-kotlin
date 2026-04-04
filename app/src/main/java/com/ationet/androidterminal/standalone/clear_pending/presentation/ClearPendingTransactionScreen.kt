package com.ationet.androidterminal.standalone.clear_pending.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.clear_pending.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.standalone.clear_pending.presentation.identification.IdentificationViewModel
import com.ationet.androidterminal.standalone.clear_pending.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.clear_pending.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_confirmation.TransactionConfirmationScreen
import com.ationet.androidterminal.standalone.clear_pending.presentation.transaction_process.TransactionProcessScreen
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import com.ationet.androidterminal.standalone.preauthorization.presentation.PreAuthorizationDestination
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

sealed interface ClearPendingTransactionDestination {
    @Serializable
    data object Identification : ClearPendingTransactionDestination

    @Serializable
    data class TransactionConfirmation(val navOrigin: NavOriginClearPending, val identification: String = "") : ClearPendingTransactionDestination

    @Serializable
    data object TransactionProcess : ClearPendingTransactionDestination

    @Serializable
    data object Summary : ClearPendingTransactionDestination

    @Serializable
    data object Print : ClearPendingTransactionDestination
}

enum class NavOriginClearPending {
    PreAuthorization,
    ClearPending
}

fun NavGraphBuilder.clearPendingTransactionScreen(
    navController: NavController,
) {
    composable<ClearPendingTransactionDestination.Identification> {
        val viewModel = hiltViewModel<IdentificationViewModel>()
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

    composable<ClearPendingTransactionDestination.TransactionConfirmation>(
        typeMap = mapOf(
            typeOf<NavOriginClearPending>() to NavType.EnumType(NavOriginClearPending::class.java)
        )
    ) {
        val navOrigin = it.toRoute<ClearPendingTransactionDestination.TransactionConfirmation>().navOrigin

        TransactionConfirmationScreen(
            onBack = {
                if (navOrigin == NavOriginClearPending.ClearPending) {
                    navController.navigateAndPopUp(ClearPendingTransactionDestination.Identification)
                } else {
                    navController.navigateAndPopUp(PreAuthorizationDestination.Identification)
                }

            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onClearIt = { navController.navigateAndPopUp(ClearPendingTransactionDestination.TransactionProcess) }
        )
    }

    composable<ClearPendingTransactionDestination.TransactionProcess> {
        TransactionProcessScreen(
            onSuccess = {
                navController.navigateAndPopUp(ClearPendingTransactionDestination.Summary)
            },
            onFailure = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<ClearPendingTransactionDestination.Summary> {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(ClearPendingTransactionDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<ClearPendingTransactionDestination.Print> {
        PrintScreen {
            navController.navigateAndPopUp(StandAloneDestination.Home)
        }
    }

}