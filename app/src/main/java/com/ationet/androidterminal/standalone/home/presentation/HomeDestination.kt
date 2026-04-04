package com.ationet.androidterminal.standalone.home.presentation

import com.ationet.androidterminal.core.navigation.LoyaltyGraph
import com.ationet.androidterminal.core.navigation.MaintenanceGraph
import com.ationet.androidterminal.core.navigation.TaskGraph
import com.ationet.androidterminal.standalone.navigation.StandAloneDestination

sealed class HomeDestination(val route: Any) {
    data object Home : HomeDestination(StandAloneDestination.Home)
    data object PreAuthorization : HomeDestination(StandAloneDestination.PreAuthorization)
    data object Completion : HomeDestination(StandAloneDestination.Completion)
    data object Sale : HomeDestination(StandAloneDestination.Sale)
    data object Receipts : HomeDestination(StandAloneDestination.Receipts)
    data object BalanceEnquiry : HomeDestination(StandAloneDestination.BalanceEnquiry)
    data object ClearPendingTransactions : HomeDestination(StandAloneDestination.ClearPendingTransactions)
    data object VoidTransaction : HomeDestination(StandAloneDestination.VoidTransaction)
    data object ChangePIN : HomeDestination(StandAloneDestination.ChangePIN)
    data object BatchClose : HomeDestination(StandAloneDestination.BatchClose)
    data object Maintenance : HomeDestination(MaintenanceGraph)
    data object Task : HomeDestination(TaskGraph)
    data object Loyalty : HomeDestination(LoyaltyGraph)
}