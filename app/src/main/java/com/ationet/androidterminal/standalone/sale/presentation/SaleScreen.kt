package com.ationet.androidterminal.standalone.sale.presentation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination
import com.ationet.androidterminal.standalone.sale.presentation.confirmation.ConfirmationScreen
import com.ationet.androidterminal.standalone.sale.presentation.identification.IdentificationScreen
import com.ationet.androidterminal.standalone.sale.presentation.identification.IdentificationViewModel
import com.ationet.androidterminal.standalone.sale.presentation.operationType.OperationTypeScreen
import com.ationet.androidterminal.standalone.sale.presentation.operationType.OperationTypeViewModel
import com.ationet.androidterminal.standalone.sale.presentation.printing.PrintScreen
import com.ationet.androidterminal.standalone.sale.presentation.product_selection.ProductSelectionScreen
import com.ationet.androidterminal.standalone.sale.presentation.sku.sku_product_selection.SKUProductSelectionScreen
import com.ationet.androidterminal.standalone.sale.presentation.sku.sku_transaction_amount.SKUTransactionAmountScreen
import com.ationet.androidterminal.standalone.sale.presentation.summary.SummaryScreen
import com.ationet.androidterminal.standalone.sale.presentation.transaction_amount.TransactionAmountScreen
import com.ationet.androidterminal.standalone.sale.presentation.transaction_process.TransactionProcessScreen


fun NavGraphBuilder.saleScreen(navController: NavController) {
    composable<SaleDestination.Identification> {
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
    composable<SaleDestination.OperationType> {
        OperationTypeScreen(
            onFuelSelected = {
                navController.navigateAndPopUp(SaleDestination.ProductSelection)
            },
            onSkuSelected = {
                navController.navigateAndPopUp(SaleDestination.SKUProductSelection)
            },
            onBack = {
                navController.navigateAndPopUp(SaleDestination.Identification)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
        )
    }
    composable<SaleDestination.SKUProductSelection> {
        SKUProductSelectionScreen(
            onBack = {
                navController.navigateAndPopUp(SaleDestination.Identification)
            },
            onProductSelected = {
                navController.navigateAndPopUp(SaleDestination.SKUTransactionAmount)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
    composable<SaleDestination.ProductSelection> {
        ProductSelectionScreen(
            onBack = {
                navController.navigateAndPopUp(SaleDestination.Identification)
            },
            onProductSelected = {
                navController.navigateAndPopUp(SaleDestination.TransactionAmount)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
    composable<SaleDestination.SKUTransactionAmount> {
        SKUTransactionAmountScreen(
            onBack = {
                navController.navigateAndPopUp(SaleDestination.SKUProductSelection)
            },
            onTransactionCompleted = {
                navController.navigateAndPopUp(SaleDestination.TransactionConfirmation)
            }
        )
    }
    composable<SaleDestination.TransactionAmount> {
        TransactionAmountScreen(
            onBack = {
                navController.navigateAndPopUp(SaleDestination.ProductSelection)
            },
            onTransactionCompleted = {
                navController.navigateAndPopUp(SaleDestination.TransactionConfirmation)
            }
        )
    }
    composable<SaleDestination.TransactionConfirmation> {
        ConfirmationScreen(
            onBack = {
                navController.navigateAndPopUp(SaleDestination.TransactionAmount)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
            onConfirmed = {
                navController.navigateAndPopUp(SaleDestination.TransactionProcess)
            }
        )
    }
    composable<SaleDestination.TransactionProcess> {
        TransactionProcessScreen(
            onSuccess = {
                navController.navigateAndPopUp(SaleDestination.Summary)
            },
            onFailure = {
                navController.navigateAndPopUp(SaleDestination.Print)
            },
            navController = navController,
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            },
        )
    }
    composable<SaleDestination.Summary> {
        SummaryScreen(
            onPrint = {
                navController.navigateAndPopUp(SaleDestination.Print)
            },
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
    composable<SaleDestination.Print> {
        PrintScreen(
            onExit = {
                navController.navigateAndPopUp(StandAloneDestination.Home)
            }
        )
    }
}