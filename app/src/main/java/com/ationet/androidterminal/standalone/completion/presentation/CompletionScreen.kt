package com.ationet.androidterminal.standalone.completion.presentation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.completion.navigation.CompletionDestination
import com.ationet.androidterminal.standalone.completion.navigation.CompletionNavTypes
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationData
import com.ationet.androidterminal.standalone.completion.navigation.PreAuthorizationProductData
import com.ationet.androidterminal.standalone.completion.presentation.confirmation.TransactionAmountConfirmationScreen
import com.ationet.androidterminal.standalone.completion.presentation.identification.CompletionIdentificationScreen
import com.ationet.androidterminal.standalone.completion.presentation.identification.CompletionIdentificationViewModel
import com.ationet.androidterminal.standalone.completion.presentation.printing.CompletionPrintScreen
import com.ationet.androidterminal.standalone.completion.presentation.summary.CompletionSummaryScreen
import com.ationet.androidterminal.standalone.completion.presentation.transaction_amount.TransactionCompletionAmountScreen
import com.ationet.androidterminal.standalone.completion.presentation.transaction_to_complete.TransactionToCompleteScreen
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import kotlin.reflect.typeOf

fun NavGraphBuilder.completionScreen(
    navHostController: NavController,
) {
    composable<CompletionDestination.Identification> {
        val viewModel = hiltViewModel<CompletionIdentificationViewModel>()

        CompletionIdentificationScreen(
            onExit = {
                navHostController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onIdentificationPresent = { viewModel.setIdentification(it) },
            onNextScreen = { identification ->
                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionToCompleteConfirmation(
                        identification = identification
                    )
                )
            }
        )
    }

    composable<CompletionDestination.TransactionToCompleteConfirmation> {
        TransactionToCompleteScreen(
            onBack = {
                navHostController.navigateAndPopUp(CompletionDestination.Identification)
            },
            onNext = { preAuthorizationData ->
                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionCompletionAmount(
                        preAuthorization = preAuthorizationData
                    )
                )
            },
            onExit = {
                navHostController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<CompletionDestination.TransactionCompletionAmount>(
        typeMap = mapOf(
            typeOf<Double>() to CompletionNavTypes.double,
            typeOf<PreAuthorizationData>() to CompletionNavTypes.preAuthorizationData,
            typeOf<PreAuthorizationProductData>() to CompletionNavTypes.productData,
            typeOf<DisplayType>() to NavType.EnumType(DisplayType::class.java)
        )
    ) {
        val arguments = it.toRoute<CompletionDestination.TransactionCompletionAmount>()

        TransactionCompletionAmountScreen(
            transactionAmount = arguments.preAuthorization.amount,
            transactionQuantity = arguments.preAuthorization.quantity,
            preAuthorizationType = arguments.preAuthorization.preAuthorizationType,
            currencySymbol = arguments.preAuthorization.currencySymbol,
            quantityUnit = arguments.preAuthorization.quantityUnit,
            onBack = {
                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionToCompleteConfirmation(
                        identification = arguments.preAuthorization.identification
                    )
                )
            },
            onTransactionAmountSelected = { transactionAmount, displayType, isCompanyPrice ->
                val transactionQuantity =
                    transactionAmount / arguments.preAuthorization.product.unitPrice
                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionAmountConfirmation(
                        preAuthorization = arguments.preAuthorization,
                        requestedQuantity = transactionQuantity,
                        requestedAmount = transactionAmount,
                        preAuthorizationType = displayType,
                        isCompanyPrice = isCompanyPrice
                    )
                )
            },
            onTransactionQuantitySelected = { transactionQuantity, displayType, isCompanyPrice ->
                val transactionAmount =
                    arguments.preAuthorization.product.unitPrice * transactionQuantity

                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionAmountConfirmation(
                        preAuthorization = arguments.preAuthorization,
                        requestedQuantity = transactionQuantity,
                        requestedAmount = transactionAmount,
                        preAuthorizationType = displayType,
                        isCompanyPrice = isCompanyPrice
                    )
                )
            }
        )
    }

    composable<CompletionDestination.TransactionAmountConfirmation>(
        typeMap = mapOf(
            typeOf<Double>() to CompletionNavTypes.double,
            typeOf<PreAuthorizationData>() to CompletionNavTypes.preAuthorizationData,
            typeOf<PreAuthorizationProductData>() to CompletionNavTypes.productData,
            typeOf<DisplayType>() to NavType.EnumType(DisplayType::class.java)
        )
    ) {
        val arguments = it.toRoute<CompletionDestination.TransactionAmountConfirmation>()

        TransactionAmountConfirmationScreen(
            authorizationCode = arguments.preAuthorization.authorizationCode,
            product = arguments.preAuthorization.product.name,
            authorizedQuantity = arguments.preAuthorization.quantity,
            authorizedAmount = arguments.preAuthorization.amount,
            quantity = arguments.requestedQuantity,
            amount = arguments.requestedAmount,
            currencySymbol = arguments.preAuthorization.currencySymbol,
            quantityUnit = arguments.preAuthorization.quantityUnit,
            language = arguments.preAuthorization.language,
            isCompanyPrice = arguments.isCompanyPrice,
            preAuthorizationType = arguments.preAuthorizationType,
            onBack = {
                navHostController.navigateAndPopUp(
                    CompletionDestination.TransactionCompletionAmount(
                        preAuthorization = arguments.preAuthorization
                    )
                )
            },
            onConfirm = {
                navHostController.navigateAndPopUp(
                    CompletionDestination.CompletionSummary(
                        preAuthorization = arguments.preAuthorization,
                        requestedAmount = arguments.requestedAmount,
                        requestedQuantity = arguments.requestedQuantity,
                    )
                )
            },
            onExit = {
                navHostController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<CompletionDestination.CompletionSummary>(
        typeMap = mapOf(
            typeOf<Double>() to CompletionNavTypes.double,
            typeOf<PreAuthorizationData>() to CompletionNavTypes.preAuthorizationData,
            typeOf<PreAuthorizationProductData>() to CompletionNavTypes.productData,
            typeOf<DisplayType>() to NavType.EnumType(DisplayType::class.java)
        )
    ) {
        CompletionSummaryScreen(
            onPrint = {
                navHostController.navigateAndPopUp(CompletionDestination.Print)
            },
            onExit = {
                navHostController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }

    composable<CompletionDestination.Print> {
        CompletionPrintScreen(
            onExit = {
                navHostController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
}