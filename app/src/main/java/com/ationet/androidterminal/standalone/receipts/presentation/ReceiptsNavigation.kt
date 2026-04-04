package com.ationet.androidterminal.standalone.receipts.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ationet.androidterminal.standalone.receipts.presentation.print.ReceiptCopyScreen
import com.ationet.androidterminal.standalone.receipts.presentation.receipts_list.ReceiptListScreen

fun NavGraphBuilder.receiptsNavigation(
    navController: NavController,
    onExit: () -> Unit,
) {
    composable<ReceiptsDestination.ReceiptsList> {
        ReceiptListScreen(
            onExit = onExit,
            onReceiptSelected = { receiptId ->
                navController.navigate(ReceiptsDestination.PrintingCopy(receiptId))
            }
        )
    }

    composable<ReceiptsDestination.PrintingCopy> {
        ReceiptCopyScreen(
            onExit = onExit
        )
    }
}


