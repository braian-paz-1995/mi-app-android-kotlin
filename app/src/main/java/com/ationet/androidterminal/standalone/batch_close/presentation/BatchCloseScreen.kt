package com.ationet.androidterminal.standalone.batch_close.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.batch_close.presentation.confirmation.ConfirmationScreen
import com.ationet.androidterminal.standalone.batch_close.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.batch_close.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.batch_close.presentation.supervisor_password.EnterSupervisorPasswordScreen
import com.ationet.androidterminal.task.home.navegation.TaskDestination
import kotlinx.serialization.Serializable

sealed interface BatchCloseDestination {
    @Serializable
    data object SupervisorPassword : BatchCloseDestination
    @Serializable
    data object Confirmation : BatchCloseDestination
    @Serializable
    data object Summary : BatchCloseDestination
    @Serializable
    data object Print : BatchCloseDestination
}

fun NavGraphBuilder.batchCloseScreen(navController: NavController) {
    val exitToTask: () -> Unit = {
        navController.navigateAndPopUp(TaskDestination.Home)
    }
    composable<BatchCloseDestination.SupervisorPassword> {
        EnterSupervisorPasswordScreen(
            onBack = exitToTask,
            onSupervisorPasswordEntered = {
                navController.navigateAndPopUp(BatchCloseDestination.Confirmation)
            }
        )
    }
    composable<BatchCloseDestination.Confirmation> {
        ConfirmationScreen(
            onBack = {
                navController.navigateAndPopUp(BatchCloseDestination.SupervisorPassword)
            },
            onExit = exitToTask,
            onSuccess = {
                navController.navigateAndPopUp(BatchCloseDestination.Summary)
            },
            onFailure = {
                navController.navigateAndPopUp(BatchCloseDestination.Print)
            }
        )
    }
    composable<BatchCloseDestination.Summary> {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(BatchCloseDestination.Print)
            },
            onExit = exitToTask,
        )
    }
    composable<BatchCloseDestination.Print> {
        PrintScreen(
            onExit = exitToTask,
        )
    }
}