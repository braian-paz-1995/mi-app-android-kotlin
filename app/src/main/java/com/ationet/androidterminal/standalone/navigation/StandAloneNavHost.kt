package com.ationet.androidterminal.standalone.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.change_pin.presentation.ChangePinDestination
import com.ationet.androidterminal.core.change_pin.presentation.changePinScreen
import com.ationet.androidterminal.core.navigation.FleetGraph
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.util.navigateAndPopUp
import com.ationet.androidterminal.maintenance.navigation.MaintenanceDestination
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.BalanceEnquiryDestination
import com.ationet.androidterminal.standalone.balance_enquiry.presentation.balanceEnquiryScreen
import com.ationet.androidterminal.standalone.batch_close.presentation.BatchCloseDestination
import com.ationet.androidterminal.standalone.batch_close.presentation.batchCloseScreen
import com.ationet.androidterminal.standalone.clear_pending.presentation.ClearPendingTransactionDestination
import com.ationet.androidterminal.standalone.clear_pending.presentation.clearPendingTransactionScreen
import com.ationet.androidterminal.standalone.completion.navigation.CompletionDestination
import com.ationet.androidterminal.standalone.completion.presentation.completionScreen
import com.ationet.androidterminal.standalone.home.presentation.HomeDestination
import com.ationet.androidterminal.standalone.home.presentation.StandAloneHomeScreen
import com.ationet.androidterminal.standalone.home.presentation.StandAloneHomeViewModel
import com.ationet.androidterminal.standalone.preauthorization.presentation.PreAuthorizationDestination
import com.ationet.androidterminal.standalone.preauthorization.presentation.preAuthorizationScreen
import com.ationet.androidterminal.standalone.receipts.presentation.ReceiptsDestination
import com.ationet.androidterminal.standalone.receipts.presentation.receiptsNavigation
import com.ationet.androidterminal.standalone.sale.presentation.SaleDestination
import com.ationet.androidterminal.standalone.sale.presentation.saleScreen
import com.ationet.androidterminal.standalone.void_transaction.presentation.VoidTransactionDestination
import com.ationet.androidterminal.standalone.void_transaction.presentation.voidTransactionScreen



fun NavGraphBuilder.navigationStandAlone(navController: NavHostController) {
    navigation<FleetGraph.StandAlone>(startDestination = StandAloneDestination.Home) {
        composable<StandAloneDestination.Home> {
            BackHandler { }
            val viewModel: StandAloneHomeViewModel = hiltViewModel()
            val state = viewModel.state.collectAsStateWithLifecycle().value
            val buttonNavigation = state.bottomOptions


            if (state.isChangeController) {
                LoadingScreen(
                    loadingState = state.loadingState,
                    text = stringResource(R.string.a_new_update_has_been_received_via_terminalsmanagement),
                    onSuccess = {
                        navController.navigateAndPopUp(state.graph)
                    },
                    onFailure = { },
                    fontSize = 20.sp
                )
            } else {
                StandAloneHomeScreen(
                    onNavigation = { destination ->
                        if (destination is HomeDestination.Maintenance) {
                            navController.navigate(
                                MaintenanceDestination.SupervisorVerification(
                                    origin = MaintenanceDestination.Origin.FLEET
                                )
                            )
                        } else {
                            navController.navigateAndPopUp(destination.route)
                        }
                    },
                    buttonBottomNavigation = buttonNavigation
                )
            }

        }

        navigation<StandAloneDestination.PreAuthorization>(
            startDestination = PreAuthorizationDestination.Identification
        ) {
            preAuthorizationScreen(navController)
        }

        navigation<StandAloneDestination.Completion>(
            startDestination = CompletionDestination.Identification
        ) {
            completionScreen(
                navHostController = navController,
            )
        }

        navigation<StandAloneDestination.Sale>(
            startDestination = SaleDestination.Identification
        ) {
            saleScreen(navController)
        }

        navigation<StandAloneDestination.ClearPendingTransactions>(
            startDestination = ClearPendingTransactionDestination.Identification
        ) {
            clearPendingTransactionScreen(navController)
        }

        navigation<StandAloneDestination.Receipts>(
            startDestination = ReceiptsDestination.ReceiptsList
        ) {
            receiptsNavigation(
                navController = navController,
                onExit = {
                    navController.navigate(StandAloneDestination.Home) {
                        popUpTo<StandAloneDestination.Receipts> {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        navigation<StandAloneDestination.ChangePIN>(
            startDestination = ChangePinDestination.Identification
        ) {
            changePinScreen(
                navHostController = navController,
                onExit = { navController.navigateAndPopUp(StandAloneDestination.Home) }
            )
        }

        navigation<StandAloneDestination.BalanceEnquiry>(
            startDestination = BalanceEnquiryDestination.Identification
        ) {
            balanceEnquiryScreen(navController)
        }

        navigation<StandAloneDestination.BatchClose>(
            startDestination = BatchCloseDestination.SupervisorPassword
        ) {
            batchCloseScreen(navController)
        }

        navigation<StandAloneDestination.VoidTransaction>(
            startDestination = VoidTransactionDestination.SupervisorPassword
        ) {
            voidTransactionScreen(navController)
        }
    }
}