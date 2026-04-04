package com.ationet.androidterminal.core.change_pin.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.change_pin.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.core.change_pin.presentation.identification.IdentificationViewModel
import com.ationet.androidterminal.core.change_pin.presentation.pin_prompt.PinPromptScreen
import com.ationet.androidterminal.core.change_pin.presentation.printing.ChangePinPrintScreen
import com.ationet.androidterminal.core.change_pin.presentation.summary.ChangePinSummaryScreen
import com.ationet.androidterminal.core.change_pin.presentation.ticket.TicketPrintScreen
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp

fun NavGraphBuilder.changePinScreen(
    navHostController: NavController,
    onExit: () -> Unit
) {
    composable<ChangePinDestination.Identification> {
        val viewModel = hiltViewModel<IdentificationViewModel>()

        IdentificationScreen(
            onExit = onExit,
            onIdentificationPresent = { viewModel.setIdentification(it) },
            onNextScreen = { identification ->
                navHostController.navigateAndPopUp(
                    ChangePinDestination.PinPrompt(
                        identification = identification
                    )
                )
            }
        )
    }

    composable<ChangePinDestination.PinPrompt> {
        PinPromptScreen(
            onBack = {
                navHostController.navigateAndPopUp(ChangePinDestination.Identification)
            },
            onExit = onExit,
            onSuccess = {
                navHostController.navigateAndPopUp(ChangePinDestination.ChangePinSummary)
            },
            onFailure = {
                navHostController.navigateAndPopUp(ChangePinDestination.ChangePinSummary)
            }
        )
    }
    composable<ChangePinDestination.Ticket> {
        TicketPrintScreen(
            onPrint = {
                navHostController.navigateAndPopUp(ChangePinDestination.Print)
            },
            onExit = onExit
        )
    }

    composable<ChangePinDestination.ChangePinSummary> {
        ChangePinSummaryScreen(
            onPrint = {
                navHostController.navigateAndPopUp(ChangePinDestination.Print)
            },
            onExit = onExit
        )
    }
    composable<ChangePinDestination.Print> {
        ChangePinPrintScreen(
            onExit = onExit,
            skipOriginal = true
        )
    }
}