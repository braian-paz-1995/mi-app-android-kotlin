package com.ationet.androidterminal.core.domain.model.receipt

enum class ReceiptTransactionTypeName {
    PreAuthorization,
    Completion,
    Sale,
    VoidTransaction,
    ClearPending,
    BatchClose,
    BalanceEnquiry,
    ChangePin,
    RechargeCC,
    ReverseCC,
    ActiveGC,
    LoyaltyAccumulation,
    LoyaltyDiscounts,
    LoyaltyBalanceEnquiry,
    LoyaltyPointsRedemption,
    LoyaltyRewardsRedemption,
    LoyaltyVoidTransaction
}