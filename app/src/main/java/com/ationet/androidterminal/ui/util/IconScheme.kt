package com.ationet.androidterminal.ui.util

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.runtime.Immutable

@Immutable
data class IconScheme(
    @DrawableRes val logo: Int,
    @DrawableRes val isotype: Int,
    @DrawableRes val logoCompact: Int,
    @RawRes val loading: Int,
    @DrawableRes val balanceInquiry: Int,
    @DrawableRes val hashtag: Int,
    @DrawableRes val idea: Int,
    @DrawableRes val camera: Int,
    @DrawableRes val flashOn: Int,
    @DrawableRes val flashOff: Int,
    /* Menu */
    @DrawableRes val receipts: Int,
    @DrawableRes val balanceEnquiry: Int,
    @DrawableRes val clearPending: Int,
    @DrawableRes val voidTransaction: Int,
    @DrawableRes val changePin: Int,
    @DrawableRes val batchClose: Int,
    @DrawableRes val loyaltyAccumulation: Int,
    @DrawableRes val loyaltyBalanceEnquiry: Int,
    @DrawableRes val loyaltyPointsRedemption: Int,
    @DrawableRes val loyaltyRewardRedemption: Int,
    @DrawableRes val loyaltyBatchClose: Int,
    @DrawableRes val loyaltyVoidAccumulation: Int,
    /* Printer */
    @DrawableRes val printed: Int,
    @DrawableRes val printerIcon: Int,
    @RawRes val printing: Int,
    @DrawableRes val printingCopy: Int,
    /*Ationet Loader*/
    @RawRes val ationetLoader: Int,
    @RawRes val ationetSuccess: Int,
    @RawRes val ationetFailure: Int,
    /* Process Status */
    @RawRes val processOk: Int,
    @RawRes val processNOk: Int,
    /* Identification Reader */
    @RawRes val identificationReader: Int,
    @DrawableRes val cardInserted: Int,
    @DrawableRes val cardInsertedRemove: Int,
    @DrawableRes val cardSwiped: Int,
    @DrawableRes val cardSwipedRemove: Int,
    @DrawableRes val cardTapped: Int,
    @DrawableRes val cardTappedRemove: Int,
    /* Maintenance */
    @DrawableRes val maintenance: Int,
    @DrawableRes val changePassword: Int,
    @DrawableRes val gift: Int,
    @DrawableRes val terminalManagement: Int,
    @DrawableRes val paperPlane: Int,
    @DrawableRes val settings: Int,
    /* Fleet */
    @DrawableRes val fleet: Int,
    /* Task */
    @DrawableRes val task: Int,
    /* Loyalty */
    @DrawableRes val loyalty: Int,
    /* Pre authorization */
    @DrawableRes val preAuthorization: Int,
    @DrawableRes val pump: Int,
    /* Completion */
    @DrawableRes val completion: Int,
    /* Sale */
    @DrawableRes val sale: Int,
    /* Receipt */
    @DrawableRes val receipt: Int,
    /* Post paid */
    @DrawableRes val postPaid: Int,
    /* Pending transaction */
    @DrawableRes val pendingTransaction: Int,
    /* Delete */
    @DrawableRes val delete: Int,
    /* Edit */
    @DrawableRes val edit: Int,
    /* Empty */
    @DrawableRes val empty: Int,
    /* Clock */
    @DrawableRes val clock: Int,
    /* Error */
    @DrawableRes val readingError: Int,
    @DrawableRes val printerError: Int,
    @DrawableRes val communicationError: Int,
    /* Batch */
    @DrawableRes val batch: Int,
)