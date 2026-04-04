package com.ationet.androidterminal.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.ationet.androidterminal.R
import com.ationet.androidterminal.ui.util.IconScheme

val AATIconScheme = IconScheme(
    logo = R.drawable.ationet_logo,
    isotype = R.drawable.ationet_isotype1,
    logoCompact = R.drawable.revopos,
    loading = R.raw.loading,
    balanceInquiry = R.drawable.balance_enquiry,
    hashtag = R.drawable.hashtag,
    idea = R.drawable.idea,
    camera = R.drawable.photo_camera,
    flashOn = R.drawable.flash_on,
    flashOff = R.drawable.flash_off,
    /*Menu*/
    receipts = R.drawable.receipts,
    balanceEnquiry = R.drawable.balance_enquiry,
    clearPending = R.drawable.clear_pending,
    voidTransaction = R.drawable.void_transaction,
    changePin = R.drawable.change_pin,
    batchClose = R.drawable.batch_close,
    loyaltyAccumulation = R.drawable.loyalty_accumulation,
    loyaltyBalanceEnquiry = R.drawable.loyalty_balance_enquiry,
    loyaltyPointsRedemption = R.drawable.loyalty_points_redemption,
    loyaltyRewardRedemption = R.drawable.loyalty_reward_redemption,
    loyaltyBatchClose = R.drawable.loyalty_batch_close,
    loyaltyVoidAccumulation = R.drawable.loyalty_void_accumulation,
    /* Printer */
    printed = R.drawable.printed,
    printerIcon = R.drawable.printer_icon,
    printing = R.raw.printing_animation,
    printingCopy = R.drawable.printing_copy,
    /*Ationet loader*/
    ationetLoader = R.raw.loader_ationet,
    ationetSuccess = R.raw.loader_ationet_success,
    ationetFailure = R.raw.loader_ationet_failure,
    /* Process Status*/
    processOk = R.raw.process_ok,
    processNOk = R.raw.process_nok,
    /* Identification Reader */
    identificationReader = R.raw.reader_id,
    cardInserted = R.drawable.card_inserted,
    cardInsertedRemove = R.drawable.card_inserted_remove,
    cardSwiped = R.drawable.card_swiped,
    cardSwipedRemove = R.drawable.card_swiped_remove,
    cardTapped = R.drawable.card_tapped,
    cardTappedRemove = R.drawable.card_tapped_remove,
    /* Maintenance */
    maintenance = R.drawable.maintenance,
    changePassword = R.drawable.change_password,
    gift = R.drawable.gift,
    terminalManagement = R.drawable.terminal_management_monocolor,
    paperPlane = R.drawable.paper_plane,
    settings = R.drawable.settings,
    /* Fleet */
    fleet = R.drawable.auto_full_filled,
    /* Task */
    task = R.drawable.task,
    /* Loyalty */
    loyalty = R.drawable.loyalty,
    /* Pre authorization */
    preAuthorization = R.drawable.pre_authorization,
    pump = R.drawable.pump,
    /* Completion */
    completion = R.drawable.completion,
    /* Sale */
    sale = R.drawable.sale,
    /* Receipt */
    receipt = R.drawable.receipt,
    /* Post paid */
    postPaid = R.drawable.post_paid,
    /* Pending transaction */
    pendingTransaction = R.drawable.pending_transaction,
    /* Delete */
    delete = R.drawable.delete,
    /* Edit */
    edit = R.drawable.edit,
    /* Error */
    readingError = R.drawable.reading_error,
    printerError = R.drawable.printer_error,
    communicationError = R.drawable.connection_error,
    /* Batch */
    batch = R.drawable.batch,
    /* Clock */
    clock = R.drawable.clock,
    /* Empty */
    empty = R.drawable.empty,
)

val LocalIconScheme = staticCompositionLocalOf { AATIconScheme }

object AATIcons {
    val logo @Composable get() = LocalIconScheme.current.logo
    val isotype @Composable get() = LocalIconScheme.current.isotype
    val logoCompact @Composable get() = LocalIconScheme.current.logoCompact
    val loading @Composable get() = LocalIconScheme.current.loading
    val balanceInquiry @Composable get() = LocalIconScheme.current.balanceInquiry
    val hashtag @Composable get() = LocalIconScheme.current.hashtag
    val idea @Composable get() = LocalIconScheme.current.idea
    val camera @Composable get() = LocalIconScheme.current.camera
    val flashOn @Composable get() = LocalIconScheme.current.flashOn
    val flashOff @Composable get() = LocalIconScheme.current.flashOff

    /* Menu */
    val receipts @Composable get() = LocalIconScheme.current.receipts
    val balanceEnquiry @Composable get() = LocalIconScheme.current.balanceEnquiry
    val clearPending @Composable get() = LocalIconScheme.current.clearPending
    val voidTransaction @Composable get() = LocalIconScheme.current.voidTransaction
    val changePin @Composable get() = LocalIconScheme.current.changePin
    val batchClose @Composable get() = LocalIconScheme.current.batchClose
    val loyaltyAccumulation @Composable get() = LocalIconScheme.current.loyaltyAccumulation
    val loyaltyBalanceEnquiry @Composable get() = LocalIconScheme.current.loyaltyBalanceEnquiry
    val loyaltyPointsRedemption @Composable get() = LocalIconScheme.current.loyaltyPointsRedemption
    val loyaltyRewardRedemption @Composable get() = LocalIconScheme.current.loyaltyRewardRedemption
    val loyaltyBatchClose @Composable get() = LocalIconScheme.current.loyaltyBatchClose
    val loyaltyVoidAccumulation @Composable get() = LocalIconScheme.current.loyaltyVoidAccumulation

    /* Printer */
    val printed @Composable get() = LocalIconScheme.current.printed
    val printerIcon @Composable get() = LocalIconScheme.current.printerIcon
    val printing @Composable get() = LocalIconScheme.current.printing
    val printingCopy @Composable get() = LocalIconScheme.current.printingCopy

    /* Ationet Loader */
    val ationetLoader @Composable get() = LocalIconScheme.current.ationetLoader
    val ationetSuccess @Composable get() = LocalIconScheme.current.ationetSuccess
    val ationetFailure @Composable get() = LocalIconScheme.current.ationetFailure

    /* Process Status */
    val processOk @Composable get() = LocalIconScheme.current.processOk

    val processNOk @Composable get() = LocalIconScheme.current.processNOk

    /* Identification Reader */
    val identificationReader @Composable get() = LocalIconScheme.current.identificationReader
    val cardInserted @Composable get() = LocalIconScheme.current.cardInserted
    val cardInsertedRemove @Composable get() = LocalIconScheme.current.cardInsertedRemove
    val cardSwiped @Composable get() = LocalIconScheme.current.cardSwiped
    val cardSwipedRemove @Composable get() = LocalIconScheme.current.cardSwipedRemove
    val cardTapped @Composable get() = LocalIconScheme.current.cardTapped
    val cardTappedRemove @Composable get() = LocalIconScheme.current.cardTappedRemove

    /* Maintenance */
    val maintenance @Composable get() = LocalIconScheme.current.maintenance
    val changePassword @Composable get() = LocalIconScheme.current.changePassword
    val gift @Composable get() = LocalIconScheme.current.gift
    val terminalManagement @Composable get() = LocalIconScheme.current.terminalManagement
    val paperPlane @Composable get() = LocalIconScheme.current.paperPlane
    val settings @Composable get() = LocalIconScheme.current.settings

    /* Fleet */
    val fleet @Composable get() = LocalIconScheme.current.fleet

    /* task */
    val task @Composable get() = LocalIconScheme.current.task

    /* loyalty */
    val loyalty @Composable get() = LocalIconScheme.current.loyalty

    /* Pre authorization */
    val preAuthorization @Composable get() = LocalIconScheme.current.preAuthorization
    val pump @Composable get() = LocalIconScheme.current.pump

    /* Completion */
    val completion @Composable get() = LocalIconScheme.current.completion

    /* Sale */
    val sale @Composable get() = LocalIconScheme.current.sale

    /* Receipt */
    val receipt @Composable get() = LocalIconScheme.current.receipt

    /* PostPay */
    val postPaid @Composable get() = LocalIconScheme.current.postPaid

    /* Pending transaction */
    val pendingTransaction @Composable get() = LocalIconScheme.current.pendingTransaction

    /* Delete */
    val delete @Composable get() = LocalIconScheme.current.delete

    /* Edit */
    val edit @Composable get() = LocalIconScheme.current.edit

    /* Error */
    val readingError @Composable get() = LocalIconScheme.current.readingError
    val printerError @Composable get() = LocalIconScheme.current.printerError
    val communicationError @Composable get() = LocalIconScheme.current.communicationError

    /* Batch */
    val batch @Composable get() = LocalIconScheme.current.batch

    /* Clock */
    val clock @Composable get() = LocalIconScheme.current.clock

    /* Empty */
    val empty @Composable get() = LocalIconScheme.current.empty

}