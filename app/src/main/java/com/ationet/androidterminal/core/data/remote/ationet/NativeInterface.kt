package com.ationet.androidterminal.core.data.remote.ationet

import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.IdentityData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.data.remote.ationet.model.ProductData
import com.ationet.androidterminal.core.util.LocalDateTimeUtils
import com.ationet.androidterminal.loyalty.loyalty_accumulation.data.local.AccumulationOption
import kotlinx.datetime.LocalDateTime

const val PreAuthorizationTransactionCode: String = "100"
const val CompletionTransactionCode: String = "120"
const val ContingencyTransactionCode: String = "126"
const val CancellationTransactionCode: String = "400"
const val ChangePinTransactionCode: String = "300"
const val ActiveGCTransactionCode: String = "802"
const val RechargeCCTransactionCode: String = "820"
const val ReverseCCTransactionCode: String = "821"
const val SaleTransactionCode: String = "200"
const val BalanceInquiryTransactionCode: String = "102"
const val VoidTransactionCode: String = "220"
const val BatchCloseCode: String = "500"
const val ModulesCode: String = "515"
const val AccumulationTransactionCode: String = "510"
const val DiscountsTransactionCode: String = "560"
const val LoyaltyBalanceEnquiryTransactionCode: String = "530"
const val LoyaltyPointsRedemptionTransactionCode: String = "520"
const val LoyaltyRewardsRedemptionTransactionCode: String = "525"
const val LoyaltyVoidTransactionCode: String = "514"
const val ProcessingModeHost: String = "1"
const val ApplicationTypeFleetControl: String = "FCS"
const val ApplicationTypeLoyaltyControl: String = "LTY"
const val CurrencyCodeARS: String = "ARS"
const val UnitCodeLiters: String = "l"
const val AccountTypeFleetTrack: String = "1"
const val AccountTypeLoyaltyTrack: String = "5"
const val MessageVersion: String = "1.3"

const val DeviceTypeIdentifierOtherSelfService: String = "4"
const val ManualEntryMethod: String = "M"
const val SwipeEntryMethod: String = "S"

enum class OperationType(val value: String) {
    Fleet("0"),
    OldGiftCard("1"),
    ConsumerCard("2"),
    GiftCard("3"),

}

val LocalDateTime.transactionDate: String
    get() = LocalDateTimeUtils.convertToDateTimeFormat("yyyyMMdd").format(this)

val LocalDateTime.transactionTime: String
    get() = LocalDateTimeUtils.convertToDateTimeFormat("HHmmss").format(this)


interface NativeInterface {
    /**
     * Request sale pre-authorization(100)
     * */
    suspend fun requestPreAuthorization(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        pumpNumber: Int,
        transactionSequenceNumber: Long,
        productCode: String,
        unitPrice: Double,
        productAmount: Double? = null,
        productQuantity: Double? = null,
        primaryTrack: String,
        primaryPin: String? = null,
        secondaryTrack: String? = null,
        secondaryPin: String? = null,
        invoice: String? = null,
        manualEntry: Boolean,
        customerData: Map<String, String?>? = null,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        dealerData: DealerData? = null
    ): NativeRequest?

    /**
     * Request for sale completion(120)
     * */
    suspend fun requestCompletion(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String,
        transactionSequenceNumber: Long,
        pumpNumber: Int,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        invoice: String,
        primaryTrack: String,
        primaryPin: String? = null,
        secondaryTrack: String? = null,
        secondaryPin: String? = null,
        manualEntry: Boolean,
        customerData: Map<String, String?>? = null,
        localDateTime: LocalDateTime,
        originalData: OriginalData?,
        currencyCode: String,
        dealerData: DealerData? = null
    ): NativeRequest?

    /**
     * Request for sale contingency (126)
     * */
    suspend fun requestContingency(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String,
        transactionSequenceNumber: Long,
        pumpNumber: Int,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        invoice: String,
        primaryTrack: String,
        manualEntry: Boolean,
        customerData: Map<String, String?>? = null,
        localDateTime: LocalDateTime,
    ): NativeRequest?

    /**
     * Request for authorization cancellation (400)
     * */
    suspend fun requestCancellation(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        localDateTime: LocalDateTime,
        manualEntry: Boolean,
        originalAuthorizationCode: String,
        originalTransactionSequenceNumber: Long,
        originalLocalDateTime: LocalDateTime,
        currencyCode: String
    ): NativeRequest?

    /**
     * Request for change pin (300)
     */
    suspend fun requestChangePin(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        primaryPin: String,
        oldPin: String,
        newPin: String,
        confirmationPin: String
    ): NativeRequest?

    /**
     * Request for rechargeCc (820)
     */
    suspend fun requestRechargeCC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String? = null,
        password: String? = null,

        ): NativeRequest?

    /**
     * Request for Accumulation (510)
     */
    suspend fun requestLoyaltyAccumulation(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        productCode: String,
        productUnitPrice: Double,
        productAmount: Double?,
        productQuantity: Double?,
        primaryTrack: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        username: String? = null,
        password: String? = null,
        paymentMethod: String?,
        optionType: AccumulationOption?,
        identityData: IdentityData?
    ): NativeRequest?

    /**
     * Request for requestLoyaltyBalanceEnquiry (530)
     */
    suspend fun requestLoyaltyBalanceEnquiry(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        primaryTrack: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        currencyCode: String,
        username: String? = null,
        password: String? = null,
    ): NativeRequest?

    /**
     * Request for requestLoyaltyPointsRedemption (520)
     */
    suspend fun requestLoyaltyPointsRedemption(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        productCode: String,
        productUnitPrice: Double,
        productAmount: Double?,
        productQuantity: Double?,
        primaryTrack: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        username: String? = null,
        password: String? = null,
        paymentMethod: String? = null,
    ): NativeRequest?

    /**
     * Request for requestLoyaltyBalanceEnquiry (580)
     */
    suspend fun requestLoyaltyRewardsRedemption(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        productCode: String,
        productUnitPrice: Double,
        productAmount: Double?,
        productQuantity: Double?,
        primaryTrack: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        username: String? = null,
        password: String? = null,
    ): NativeRequest?

    /**
     * Request for loyalty void transaction
     */
    suspend fun requestLoyaltyVoidTransaction(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        primaryTrack: String,
        originalData: OriginalData?
    ): NativeRequest?

    /**
     * Request for reverseCc (821)
     */
    suspend fun requestReverseCC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String? = null,
        password: String? = null,
        currencyCode: String
    ): NativeRequest?

    /**
     * Request for ActiveGC (802)
     */
    suspend fun requestActiveGC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String? = null,
        password: String? = null,
        currencyCode: String
    ): NativeRequest?

    /**
     * Request for postpaid sales (200)
     */
    suspend fun requestPostpaidSale(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String? = null,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        pumpNumber: Int,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        primaryTrack: String,
        primaryPin: String?,
        secondaryPin: String?,
        secondaryTrack: String?,
        invoice: String,
        customerData: Map<String, String?>?,
        currencyCode: String,
        operationType: Boolean
    ): NativeRequest?

    /**
     * Request for sales (200)
     * */
    suspend fun requestSale(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        productCode: String,
        productAmount: Double,
        productQuantity: Double,
        unitPrice: Double,
        transactionAmount: Double,
        primaryTrack: String,
        primaryPin: String,
        secondaryPin: String?,
        secondaryTrack: String?,
        invoice: String,
        customerData: Map<String, String?>?,
        currencyCode: String,
        dealerData: DealerData? = null,
        operationType: Boolean
    ): NativeRequest?

    /**
     * Request for GNC sales (200)
     * */
    suspend fun requestGncSale(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        pumpNumber: Int,
        productData: List<ProductData>,
        primaryTrack: String,
        primaryPin: String,
        secondaryPin: String?,
        secondaryTrack: String?,
        invoice: String,
        customerData: Map<String, String?>?
    ): NativeRequest?

    /**
     * Request for Balance Inquiry (102)
     */
    suspend fun requestBalanceInquiry(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localDateTime: LocalDateTime,
        manualEntry: Boolean,
        primaryTrack: String,
        pumpNumber: Int,
        productCode: String,
        unitPrice: Double
    ): NativeRequest?

    /**
     * Request for void transaction
     */
    suspend fun requestVoidTransaction(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        primaryTrack: String,
        originalData: OriginalData?
    ): NativeRequest?

    /**
     * Request for batch close
     * */
    suspend fun requestBatchClose(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        batchNumber: String,
        salesCounter: Int,
        salesTotal: Double,
        voidedCounter: Int,
        voidedTotal: Double
    ): NativeRequest?

    /**
     * Request for modules
     * */
    suspend fun requestModules(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        messageFormatVersion: String,
        subscriberCode: String,
        lastVersion: Int
    ): NativeRequest?
}