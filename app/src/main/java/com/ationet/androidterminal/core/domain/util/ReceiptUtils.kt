package com.ationet.androidterminal.core.domain.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName

fun getTransactionTypeName(context: Context, transactionTypeName: ReceiptTransactionTypeName): String {
    return with(ContextCompat.getContextForLanguage(context)) {
        when (transactionTypeName) {
            ReceiptTransactionTypeName.PreAuthorization -> getString(R.string.receipt_pre_authorization_title)
            ReceiptTransactionTypeName.Completion -> getString(R.string.receipt_completion_title)
            ReceiptTransactionTypeName.Sale -> getString(R.string.receipt_sale_title)
            ReceiptTransactionTypeName.VoidTransaction -> getString(R.string.receipt_void_transaction)
            ReceiptTransactionTypeName.ClearPending -> getString(R.string.receipt_completion_title)
            ReceiptTransactionTypeName.BalanceEnquiry -> getString(R.string.balance_enquiry)
            ReceiptTransactionTypeName.BatchClose -> getString(R.string.receipt_batch_close)
            ReceiptTransactionTypeName.ChangePin -> getString(R.string.receipt_change_pin)
            ReceiptTransactionTypeName.RechargeCC -> getString(R.string.recharge_cc)
            ReceiptTransactionTypeName.ReverseCC -> getString(R.string.reverse_cc)
            ReceiptTransactionTypeName.ActiveGC -> getString(R.string.active_gc)
            ReceiptTransactionTypeName.LoyaltyAccumulation -> getString(R.string.loyalty_accumulation)
            ReceiptTransactionTypeName.LoyaltyDiscounts -> getString(R.string.loyalty_discounts)
            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> getString(R.string.loyalty_balance_enquiry)
            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> getString(R.string.loyalty_points_redemption)
            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> getString(R.string.loyalty_rewards_redemption)
            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> getString(R.string.loyalty_void_accumulation)

        }
    }
}