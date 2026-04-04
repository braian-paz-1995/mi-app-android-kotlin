package com.ationet.androidterminal.standalone.void_transaction.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import com.ationet.androidterminal.standalone.void_transaction.presentation.authorization_code.EnterAuthorizationCodeScreen
import com.ationet.androidterminal.standalone.void_transaction.presentation.confirmation.ConfirmationScreen
import com.ationet.androidterminal.standalone.void_transaction.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.void_transaction.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.void_transaction.presentation.supervisor_password.EnterSupervisorPasswordScreen

fun NavGraphBuilder.voidTransactionScreen(navController: NavController) {
    composable<VoidTransactionDestination.SupervisorPassword> {
        EnterSupervisorPasswordScreen(
            onBack = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onSupervisorPasswordEntered = {
                navController.navigateAndPopUp(VoidTransactionDestination.AuthorizationCode)
            }
        )
    }
    composable<VoidTransactionDestination.AuthorizationCode> {
        EnterAuthorizationCodeScreen(
            onBack = {
                navController.navigateAndPopUp(VoidTransactionDestination.SupervisorPassword)
            },
            onAuthorizationCodeEntered = { authorizationCode, typeTransaction ->
                navController.navigateAndPopUp(VoidTransactionDestination.Confirmation(authorizationCode, typeTransaction))
            }
        )
    }
    composable<VoidTransactionDestination.Confirmation> {
        ConfirmationScreen(
            onBack = {
                navController.navigateAndPopUp(VoidTransactionDestination.AuthorizationCode)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onSuccess = {
                navController.navigateAndPopUp(VoidTransactionDestination.Summary(it))
            },
            onFailure = {
                navController.navigateAndPopUp(VoidTransactionDestination.Print)
            },
        )
    }

    composable<VoidTransactionDestination.Summary> {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(VoidTransactionDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
    composable<VoidTransactionDestination.Print> {
        PrintScreen(
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
}