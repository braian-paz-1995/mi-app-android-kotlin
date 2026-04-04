package com.ationet.androidterminal.standalone.receipts.domain.receipts


import com.ationet.androidterminal.core.change_pin.domain.receipt.ChangePinReceiptPrinter
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.domain.receipt.ReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_accumulation.domain.receipt.LoyaltyAccumulationReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_accumulation.domain.receipt.LoyaltyDiscountsReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_balance_enquiry.domain.receipt.LoyaltyBalanceEnquiryReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_points_redemption.domain.receipt.LoyaltyPointsRedemptionReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_rewards_redemption.domain.receipt.LoyaltyRewardsRedemptionReceiptPrinter
import com.ationet.androidterminal.loyalty.loyalty_void_transaction.domain.receipt.LoyaltyVoidReceiptPrinter
import com.ationet.androidterminal.standalone.balance_enquiry.domain.receipt.BalanceEnquiryReceiptPrinter
import com.ationet.androidterminal.standalone.batch_close.domain.receipt.BatchReceiptPrinter
import com.ationet.androidterminal.standalone.clear_pending.domain.receipt.ClearPendingReceiptPrinter
import com.ationet.androidterminal.standalone.completion.domain.receipt.CompletionReceiptPrinter
import com.ationet.androidterminal.standalone.preauthorization.domain.receipt.PreAuthorizationReceiptPrinter
import com.ationet.androidterminal.standalone.sale.domain.SaleReceiptPrinter
import com.ationet.androidterminal.standalone.void_transaction.domain.receipt.VoidReceiptPrinter
import com.ationet.androidterminal.task.activeGC.domain.receipt.ActiveGCReceiptPrinter
import com.ationet.androidterminal.task.rechargeCC.domain.receipt.RechargeCCReceiptPrinter
import com.ationet.androidterminal.task.reverseCC.domain.receipt.ReverseCCReceiptPrinter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptPrinterFactory @Inject constructor(
    @StandAloneReceiptPrinter private val printers: Set<@JvmSuppressWildcards ReceiptPrinter>
) {
    fun getReceiptPrinterTransactionName(name: ReceiptTransactionTypeName): ReceiptPrinter {
        return when (name) {
            ReceiptTransactionTypeName.PreAuthorization -> printers.ofType<PreAuthorizationReceiptPrinter>()
            ReceiptTransactionTypeName.Completion -> printers.ofType<CompletionReceiptPrinter>()
            ReceiptTransactionTypeName.Sale -> printers.ofType<SaleReceiptPrinter>()
            ReceiptTransactionTypeName.VoidTransaction -> printers.ofType<VoidReceiptPrinter>()
            ReceiptTransactionTypeName.ClearPending -> printers.ofType<ClearPendingReceiptPrinter>()
            ReceiptTransactionTypeName.BatchClose -> printers.ofType<BatchReceiptPrinter>()
            ReceiptTransactionTypeName.BalanceEnquiry -> printers.ofType<BalanceEnquiryReceiptPrinter>()
            ReceiptTransactionTypeName.ChangePin -> printers.ofType<ChangePinReceiptPrinter>()
            ReceiptTransactionTypeName.RechargeCC -> printers.ofType<RechargeCCReceiptPrinter>()
            ReceiptTransactionTypeName.ReverseCC -> printers.ofType<ReverseCCReceiptPrinter>()
            ReceiptTransactionTypeName.ActiveGC -> printers.ofType<ActiveGCReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyAccumulation -> printers.ofType<LoyaltyAccumulationReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyDiscounts -> printers.ofType<LoyaltyDiscountsReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> printers.ofType<LoyaltyBalanceEnquiryReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> printers.ofType<LoyaltyPointsRedemptionReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> printers.ofType<LoyaltyRewardsRedemptionReceiptPrinter>()
            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> printers.ofType<LoyaltyVoidReceiptPrinter>()
        }
    }

    private inline fun <reified T : ReceiptPrinter> Set<ReceiptPrinter>.ofType(): ReceiptPrinter =
        first { it is T }
}