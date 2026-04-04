package com.ationet.androidterminal.standalone.balance_enquiry.presentation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.Quantity
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.identification.IdentificationViewModel
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.product_selection.ProductSelectionScreen
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryData
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.transaction_process.TransactionProcessScreen
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.util.BalanceEnquiryNavType
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import kotlinx.datetime.LocalDateTime
import kotlin.reflect.typeOf

fun NavGraphBuilder.balanceEnquiryScreen(navController: NavController) {
    composable<BalanceEnquiryDestination.Identification> {
        val viewModel = hiltViewModel<IdentificationViewModel>()

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
            navController = navController,
        )
    }
    composable<BalanceEnquiryDestination.ProductSelection> {
        ProductSelectionScreen(
            onBack = {
                navController.navigateAndPopUp(BalanceEnquiryDestination.Identification)
            },
            onProductSelected = {
                navController.navigateAndPopUp(BalanceEnquiryDestination.TransactionProcess)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<BalanceEnquiryDestination.TransactionProcess> {
        TransactionProcessScreen(
            onSuccess = {
                navController.navigateAndPopUp(BalanceEnquiryDestination.Summary(it))
            },
            onFailure = {
                navController.navigateAndPopUp(BalanceEnquiryDestination.Print)
            }
        )
    }
    composable<BalanceEnquiryDestination.Summary>(
        typeMap = mapOf(
            typeOf<Double>() to BalanceEnquiryNavType.Double,
            typeOf<SummaryData>() to BalanceEnquiryNavType.SummaryData,
            typeOf<Configuration.LanguageType>() to NavType.EnumType(Configuration.LanguageType::class.java),
            typeOf<Quantity.InputType>() to NavType.EnumType(Quantity.InputType::class.java),
            typeOf<LocalDateTime>() to BalanceEnquiryNavType.LocalDateTime
        )
    ) {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(BalanceEnquiryDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
    composable<BalanceEnquiryDestination.Print> {
        PrintScreen(
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
}