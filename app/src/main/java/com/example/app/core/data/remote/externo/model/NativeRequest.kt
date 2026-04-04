package com.ationet.androidterminal.core.data.remote.ationet.model

import com.ationet.androidterminal.core.data.remote.ationet.AccountTypeFleetTrack
import com.ationet.androidterminal.core.data.remote.ationet.AccountTypeLoyaltyTrack
import com.ationet.androidterminal.core.data.remote.ationet.AccumulationTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ActiveGCTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ApplicationTypeFleetControl
import com.ationet.androidterminal.core.data.remote.ationet.ApplicationTypeLoyaltyControl
import com.ationet.androidterminal.core.data.remote.ationet.BalanceInquiryTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.BatchCloseCode
import com.ationet.androidterminal.core.data.remote.ationet.CancellationTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ChangePinTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.CompletionTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ContingencyTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.CurrencyCodeARS
import com.ationet.androidterminal.core.data.remote.ationet.DeviceTypeIdentifierOtherSelfService
import com.ationet.androidterminal.core.data.remote.ationet.DiscountsTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.LoyaltyBalanceEnquiryTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.LoyaltyPointsRedemptionTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.LoyaltyRewardsRedemptionTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.LoyaltyVoidTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ManualEntryMethod
import com.ationet.androidterminal.core.data.remote.ationet.MessageVersion
import com.ationet.androidterminal.core.data.remote.ationet.ModulesCode
import com.ationet.androidterminal.core.data.remote.ationet.PreAuthorizationTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ProcessingModeHost
import com.ationet.androidterminal.core.data.remote.ationet.RechargeCCTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.ReverseCCTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.SaleTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.SwipeEntryMethod
import com.ationet.androidterminal.core.data.remote.ationet.UnitCodeLiters
import com.ationet.androidterminal.core.data.remote.ationet.VoidTransactionCode
import com.ationet.androidterminal.core.data.remote.ationet.transactionDate
import com.ationet.androidterminal.core.data.remote.ationet.transactionTime
import com.ationet.androidterminal.core.domain.util.generateInvoice
import com.ationet.androidterminal.core.util.formattedAmount
import com.ationet.androidterminal.core.util.formattedUnitPrice
import com.ationet.androidterminal.core.util.formattedVolume
import com.ationet.androidterminal.loyalty.loyalty_accumulation.data.local.AccumulationOption
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeRequest(
    @SerialName("ProcessingMode")
    val processingMode: String? = null,
    @SerialName("TerminalIdentification")
    val terminalIdentification: String? = null,
    @SerialName("TerminalCode")
    val terminalCode: String? = null,
    @SerialName("SystemModel")
    val systemModel: String? = null,
    @SerialName("SystemVersion")
    val systemVersion: String? = null,
    @SerialName("TransactionCode")
    val transactionCode: String? = null,
    @SerialName("ActionCode")
    val actionCode: String? = null,
    @SerialName("SubscriberCode")
    val subscriberCode: String? = null,
    @SerialName("NetworkCode")
    val networkCode: String? = null,
    @SerialName("TransactionSequenceNumber")
    val transactionSequenceNumber: String? = null,
    @SerialName("Balance")
    val balance: String? = null,
    @SerialName("EntryMethod")
    val entryMethod: String? = null,
    @SerialName("CurrencyCode")
    val currencyCode: String? = null,
    @SerialName("UnitCode")
    val unitCode: String? = null,
    @SerialName("LocalTransactionDate")
    val localTransactionDate: String? = null,
    @SerialName("LocalTransactionTime")
    val localTransactionTime: String? = null,
    @SerialName("LocalDate")
    val localDate: String? = null,
    @SerialName("LocalTime")
    val localTime: String? = null,
    @SerialName("ApplicationType")
    val applicationType: String? = null,
    @SerialName("AccountType")
    val accountType: String? = null,
    @SerialName("WithdrawalCode")
    val withdrawalCode: String? = null,
    @SerialName("MessageFormatVersion")
    val messageFormatVersion: String? = null,
    @SerialName("DeviceTypeIdentifier")
    val deviceTypeIdentifier: String? = null,
    @SerialName("IdentificationId")
    val identificationId: String? = null,
    @SerialName("CustomerPan")
    val customerPan: String? = null,
    @SerialName("CustomerLabel")
    val customerLabel: String? = null,
    @SerialName("Label")
    val label: String? = null,
    @SerialName("Pan")
    val pan: String? = null,
    @SerialName("MerchantCode")
    val merchantCode: String? = null,
    @SerialName("CompanyCode")
    val companyCode: String? = null,
    @SerialName("ContractCode")
    val contractCode: String? = null,
    @SerialName("OperationType")
    val operationType: String? = null,
    //region authorization
    @SerialName("AvailableBalance")
    val availableBalance: Double? = null,
    @SerialName("PumpNumber")
    val pumpNumber: String? = null,
    @SerialName("ProductCode")
    val productCode: String? = null,
    @SerialName("ProductUnitPrice")
    val productUnitPrice: String? = null,
    @SerialName("ProductNetAmount")
    val productNetAmount: String? = null,
    @SerialName("ProductTaxes")
    val productTaxes: String? = null,
    @SerialName("ProductAmount")
    val productAmount: String? = null,
    @SerialName("ProductQuantity")
    val productQuantity: String? = null,
    @SerialName("TransactionAmount")
    val transactionAmount: String? = null,
    @SerialName("TransactionExtendedData")
    val transactionExtendedData: String? = null,
    @SerialName("TransactionNetAmount")
    val transactionNetAmount: String? = null,
    @SerialName("AuthorizationCode")
    val authorizationCode: String? = null,
    @SerialName("PrimaryTrack")
    val primaryTrack: String? = null,
    @SerialName("PrimaryPin")
    val primaryPin: String? = null,
    @SerialName("SecondaryTrack")
    val secondaryTrack: String? = null,
    @SerialName("SecondaryPin")
    val secondaryPin: String? = null,
    @SerialName("CustomerData")
    val customerData: Map<String, String?>? = null,
    @SerialName("OriginalData")
    val originalData: OriginalData? = null,
    @SerialName("ServiceCode")
    val serviceCode: String? = null,
    @SerialName("BatchNumber")
    val batchNumber: String? = null,
    @SerialName("ShiftNumber")
    val shiftNumber: String? = null,
    @SerialName("InvoiceNumber")
    val invoiceNumber: String? = null,
    @SerialName("DealerData")
    val dealerData: DealerData? = null,
    @SerialName("TrackNumber")
    val trackNumber: String? = null,
    //endregion
    //region change pin
    @SerialName("Track")
    val track: String? = null,
    @SerialName("OldPin")
    val oldPin: String? = null,
    @SerialName("NewPin")
    val newPin: String? = null,
    @SerialName("ConfirmationPin")
    val confirmationPin: String? = null,
    //endregion
    //region sale gnc
    @SerialName("ProductData")
    val productData: List<ProductData?>? = null,
    //endregion
    @SerialName("ResponseCode")
    val responseCode: String? = null,
    @SerialName("ResponseText")
    val responseText: String? = null,
    @SerialName("LongResponseText")
    val longResponseText: String? = null,
    @SerialName("ResponseMessage")
    val responseMessage: String? = null,
    @SerialName("ResponseError")
    val responseError: String? = null,
    @SerialName("ReceiptData")
    val receiptData: String? = null,
    /*Mobile Payment*/
    @SerialName("TransactionId")
    val transactionId: String? = null,
    @SerialName("DispatchedAmount")
    val dispatchedAmount: String? = null,
    @SerialName("UnitPrice")
    val unitPrice: String? = null,
    @SerialName("Quantity")
    val quantity: String? = null,
    @SerialName("LastVersion")
    val lastVersion: Int? = 0,
    /*RechargeCC*/
    @SerialName("Amount")
    val amount: Double? = null,
    /* Price modifiers */
    @SerialName("CompanyPrice")
    val companyPrice: PriceData? = null,
    @SerialName("MerchantPrice")
    val merchantPrice: PriceData? = null,
    @SerialName("SalesCounter")
    val salesCounter: Int? = null,
    @SerialName("SalesTotal")
    val salesTotal: Double? = null,
    @SerialName("VoidedCounter")
    val voidedCounter: Int? = null,
    @SerialName("VoidedTotal")
    val voidedTotal: Double? = null,
    @SerialName("DeviceType")
    val deviceType: String? = null,
    @SerialName("Username")
    val username: String? = null,
    @SerialName("Password")
    val password: String? = null,
    /*Loyalty*/
    @SerialName("LoyaltyTransactionData")
    val loyaltyTransactionData: LoyaltyTransactionData? = null,
    @SerialName("IdentityData")
    val identityData: IdentityData? = null,
    @SerialName("LoyaltyDiscount")
    val loyaltyDiscount: Double? = null,
    /*Modules*/
    @SerialName("SupportsCardsB2C")
    val supportsCardsB2C: Boolean? = null,
    @SerialName("SupportsGiftCard")
    val supportsGiftCard: Boolean? = null,
    @SerialName("SupportsFleet")
    val supportsFleet: Boolean? = null,
    @SerialName("SupportsLoyalty")
    val supportsLoyalty: Boolean? = null,
    @SerialName("SupportsOffline")
    val supportsOffline: Boolean? = null,
) {


    companion object {
        fun preAuthorization(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            authorizationCode: String?,
            pumpNumber: Int,
            transactionSequenceNumber: Long,
            productCode: String,
            unitPrice: Double,
            productAmount: Double?,
            productQuantity: Double?,
            primaryTrack: String,
            primaryPin: String?,
            secondaryTrack: String?,
            secondaryPin: String?,
            invoice: String?,
            manualEntry: Boolean,
            customerData: Map<String, String?>?,
            localDateTime: LocalDateTime,
            unitCode: String,
            currencyCode: String,
            dealerData: DealerData?
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = PreAuthorizationTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = currencyCode,
                unitCode = unitCode,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                pumpNumber = pumpNumber.toString(),
                productCode = productCode,
                productUnitPrice = unitPrice.formattedUnitPrice,
                productNetAmount = null,
                productTaxes = null,
                productAmount = productAmount.formattedAmount,
                productQuantity = productQuantity.formattedVolume,
                transactionAmount = null,
                transactionNetAmount = null,
                authorizationCode = authorizationCode,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryTrack = secondaryTrack,
                secondaryPin = secondaryPin,
                customerData = customerData,
                originalData = OriginalData(),
                serviceCode = null,
                batchNumber = null,
                shiftNumber = null,
                transactionExtendedData = null,
                invoiceNumber = invoice?.let { number -> generateInvoice(terminalId, number) },
                dealerData = dealerData
            )
        }

        fun completion(
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
            invoice: String,
            primaryTrack: String,
            primaryPin: String?,
            secondaryTrack: String?,
            secondaryPin: String?,
            manualEntry: Boolean,
            customerData: Map<String, String?>?,
            localDateTime: LocalDateTime,
            originalData: OriginalData?,
            currencyCode: String,
            dealerData: DealerData?
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = CompletionTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = currencyCode,
                unitCode = UnitCodeLiters,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                pumpNumber = pumpNumber.toString(),
                productCode = productCode,
                productUnitPrice = unitPrice.formattedUnitPrice,
                productNetAmount = null,
                productTaxes = null,
                productAmount = productAmount.formattedAmount,
                productQuantity = productQuantity.formattedVolume,
                transactionAmount = productAmount.formattedAmount,
                transactionNetAmount = null,
                authorizationCode = authorizationCode,
                primaryTrack = primaryTrack,
                primaryPin = primaryPin,
                secondaryTrack = secondaryTrack,
                secondaryPin = secondaryPin,
                customerData = customerData,
                originalData = originalData ?: OriginalData(),
                serviceCode = null,
                batchNumber = null,
                shiftNumber = null,
                transactionExtendedData = null,
                invoiceNumber = generateInvoice(terminalId, invoice),
                dealerData = dealerData
            )
        }

        fun contingency(
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
            invoice: String,
            primaryTrack: String,
            manualEntry: Boolean,
            customerData: Map<String, String?>?,
            localDateTime: LocalDateTime
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = ContingencyTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = CurrencyCodeARS,
                unitCode = UnitCodeLiters,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                pumpNumber = pumpNumber.toString(),
                productCode = productCode,
                productUnitPrice = unitPrice.formattedUnitPrice,
                productNetAmount = null,
                productTaxes = null,
                productAmount = productAmount.formattedAmount,
                productQuantity = productQuantity.formattedVolume,
                transactionAmount = productAmount.formattedAmount,
                transactionNetAmount = null,
                authorizationCode = authorizationCode,
                primaryTrack = primaryTrack,
                primaryPin = null,
                secondaryTrack = null,
                secondaryPin = null,
                customerData = customerData,
                originalData = OriginalData(),
                serviceCode = null,
                batchNumber = null,
                shiftNumber = null,
                transactionExtendedData = null,
                invoiceNumber = generateInvoice(terminalId, invoice)
            )
        }

        fun cancellation(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            authorizationCode: String?,
            transactionSequenceNumber: Long,
            localDateTime: LocalDateTime,
            manualEntry: Boolean,
            originalAuthorizationCode: String,
            originalTransactionSequenceNumber: Long,
            originalLocalDateTime: LocalDateTime,
            currencyCode: String
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = CancellationTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = currencyCode,
                unitCode = UnitCodeLiters,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                authorizationCode = authorizationCode,
                originalData = OriginalData(
                    transactionCode = PreAuthorizationTransactionCode, // This method only cancels pre authorizations. refactor when necessary
                    authorizationCode = originalAuthorizationCode,
                    transactionSequenceNumber = originalTransactionSequenceNumber.toString(),
                    localTransactionDate = originalLocalDateTime.transactionDate,
                    localTransactionTime = originalLocalDateTime.transactionTime,
                )
            )
        }

        fun pinChange(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            manualEntry: Boolean,
            localDateTime: LocalDateTime,
            track: String,
            primaryPin: String,
            oldPin: String,
            newPin: String,
            confirmationPin: String
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = ChangePinTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = CurrencyCodeARS,
                unitCode = UnitCodeLiters,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                track = track,
                primaryPin = primaryPin,
                oldPin = oldPin,
                newPin = newPin,
                confirmationPin = confirmationPin
            )
        }

        fun rechargeCC(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            track: String,
            amount: Double,


            ): NativeRequest {
            return NativeRequest(
                systemModel = systemModel,
                systemVersion = systemVersion,
                actionCode = RechargeCCTransactionCode,
                messageFormatVersion = MessageVersion,
                subscriberCode = terminalId.take(3).uppercase(),
                terminalCode = terminalId,
                trackNumber = track,
                amount = amount,
            )
        }

        fun loyaltyAccumulation(
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
            paymentMethod: String?,
            optionType: AccumulationOption?,
            identityData: IdentityData?
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = if (optionType == AccumulationOption.POINTS) {
                    AccumulationTransactionCode
                } else {
                    DiscountsTransactionCode
                },
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                applicationType = ApplicationTypeLoyaltyControl,
                accountType = AccountTypeLoyaltyTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = currencyCode,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                terminalIdentification = terminalId,
                primaryTrack = primaryTrack,
                transactionAmount = productAmount.formattedAmount,
                loyaltyTransactionData = LoyaltyTransactionData(
                    products = listOf(
                        LoyaltyProduct(
                            unitCode = unitCode,
                            productCode = productCode,
                            productUnitPrice = productUnitPrice,
                            productAmount = productAmount,
                            productQuantity = productQuantity,
                            productTotalAmount = productAmount,
                            productTotalQuantity = productQuantity
                        )
                    ),
                    paymentsMethods = listOf(
                        NativePaymentMethod(
                            paymentsMethodCode = paymentMethod,
                            amount = productAmount.formattedAmount
                        )
                    )
                ),
                identityData = IdentityData(
                    entryMethod = identityData?.entryMethod,
                    identityNumber = identityData?.identityNumber,
                    country = identityData?.country,
                    firstName = identityData?.firstName,
                    lastName = identityData?.lastName,
                    sex = identityData?.sex,
                    birthDate = identityData?.birthDate,
                    issueDate = identityData?.issueDate,
                    procedureNumber = identityData?.procedureNumber,
                    copy = identityData?.copy,
                )
            )
        }

        fun loyaltyBalanceEnquiry(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            primaryTrack: String,
            manualEntry: Boolean,
            localDateTime: LocalDateTime,
            currencyCode: String,
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = LoyaltyBalanceEnquiryTransactionCode,
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                applicationType = ApplicationTypeLoyaltyControl,
                accountType = AccountTypeLoyaltyTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = currencyCode,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                terminalIdentification = terminalId,
                primaryTrack = primaryTrack,

                )
        }

        fun loyaltyPointsRedemption(
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
            paymentMethod: String?,
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = LoyaltyPointsRedemptionTransactionCode,
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                applicationType = ApplicationTypeLoyaltyControl,
                accountType = AccountTypeLoyaltyTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = currencyCode,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                terminalIdentification = terminalId,
                primaryTrack = primaryTrack,
                transactionAmount = productAmount.formattedAmount,
                loyaltyTransactionData = LoyaltyTransactionData(
                    products = listOf(
                        LoyaltyProduct(
                            unitCode = unitCode,
                            productCode = productCode,
                            productUnitPrice = productUnitPrice,
                            productAmount = productAmount,
                            productQuantity = productQuantity
                        )
                    ),
                    paymentsMethods = listOf(
                        NativePaymentMethod(
                            paymentsMethodCode = paymentMethod,
                            amount = productAmount.formattedAmount
                        )
                    )
                )
            )
        }

        fun loyaltyRewardsRedemption(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            productAmount: Double?,
            withdrawalCode: String,
            manualEntry: Boolean,
            localDateTime: LocalDateTime,
            currencyCode: String,
        ): NativeRequest {
            return NativeRequest(

                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = LoyaltyRewardsRedemptionTransactionCode,
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                applicationType = ApplicationTypeLoyaltyControl,
                accountType = AccountTypeLoyaltyTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = currencyCode,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                terminalIdentification = terminalId,
                withdrawalCode = withdrawalCode,
                transactionAmount = productAmount.formattedAmount,
            )
        }

        fun loyaltyVoidTransaction(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            localTransactionDate: LocalDateTime,
            localTransactionTime: LocalDateTime,
            primaryTrack: String,
            originalData: OriginalData?
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = LoyaltyVoidTransactionCode,
                entryMethod = SwipeEntryMethod,
                applicationType = ApplicationTypeLoyaltyControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = CurrencyCodeARS,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localTransactionDate.transactionDate,
                localTransactionTime = localTransactionTime.transactionTime,
                terminalIdentification = terminalId,
                originalData = originalData,
                terminalCode = terminalId,
                primaryTrack = primaryTrack,
                authorizationCode = null,
                unitCode = null,
                pumpNumber = null,
                primaryPin = null,
                secondaryTrack = null,
                secondaryPin = null,
                transactionAmount = null,
                productCode = null,
                productUnitPrice = null,
                productAmount = null,
                productQuantity = null,
                customerData = null,
                productData = null,
                transactionNetAmount = null,
                productTaxes = null,
                invoiceNumber = null,
                serviceCode = null,
                batchNumber = null,
                transactionExtendedData = null,
                responseCode = null,
                responseText = null
            )
        }

        fun reverseCC(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            manualEntry: Boolean,
            localDateTime: LocalDateTime,
            track: String,
            currencyCode: String,
        ): NativeRequest {
            return NativeRequest(
                actionCode = ReverseCCTransactionCode,
                transactionCode = ReverseCCTransactionCode,
                terminalIdentification = terminalId,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                subscriberCode = terminalId.take(3).uppercase(),
                terminalCode = terminalId,
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = currencyCode,
                unitCode = UnitCodeLiters,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                trackNumber = track,
            )
        }

        fun activeGC(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            manualEntry: Boolean,
            localDateTime: LocalDateTime,
            track: String,
            currencyCode: String,
        ): NativeRequest {
            return NativeRequest(
                actionCode = ActiveGCTransactionCode,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = ActiveGCTransactionCode,
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = currencyCode,
                unitCode = UnitCodeLiters,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                networkCode = terminalId.take(3).uppercase(),
                processingMode = ProcessingModeHost,
                identificationId = null,
                label = null,
                pan = null,
                trackNumber = track,
                subscriberCode = terminalId.take(3).uppercase(),
                merchantCode = null,
                companyCode = null,
            )
        }

        fun postPaidSale(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            authorizationCode: String?,
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
            operationType: Boolean,
        ): NativeRequest {
            return if (operationType) {
                NativeRequest(
                    processingMode = ProcessingModeHost,
                    terminalIdentification = terminalId,
                    terminalCode = terminalId,
                    systemModel = systemModel,
                    systemVersion = systemVersion,
                    transactionCode = SaleTransactionCode,
                    transactionSequenceNumber = transactionSequenceNumber.toString(),
                    entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                    currencyCode = currencyCode,
                    localTransactionDate = localDateTime.transactionDate,
                    localTransactionTime = localDateTime.transactionTime,
                    applicationType = ApplicationTypeFleetControl,
                    accountType = AccountTypeFleetTrack,
                    messageFormatVersion = MessageVersion,
                    deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                    primaryPin = primaryPin,
                    secondaryPin = secondaryPin,
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    invoiceNumber = generateInvoice(terminalId, invoice),
                    pumpNumber = pumpNumber.toString(),
                    productCode = productCode,
                    productUnitPrice = unitPrice.formattedUnitPrice,
                    productNetAmount = null,
                    productTaxes = null,
                    productAmount = productAmount.formattedAmount,
                    productQuantity = productQuantity.formattedVolume,
                    transactionAmount = transactionAmount.formattedAmount,
                    transactionNetAmount = null,
                    authorizationCode = authorizationCode,
                    customerData = customerData
                )
            } else {
                NativeRequest(
                    processingMode = ProcessingModeHost,
                    terminalIdentification = terminalId,
                    terminalCode = terminalId,
                    systemModel = systemModel,
                    systemVersion = systemVersion,
                    transactionCode = SaleTransactionCode,
                    transactionSequenceNumber = transactionSequenceNumber.toString(),
                    entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                    currencyCode = currencyCode,
                    localTransactionDate = localDateTime.transactionDate,
                    localTransactionTime = localDateTime.transactionTime,
                    applicationType = ApplicationTypeFleetControl,
                    accountType = AccountTypeFleetTrack,
                    messageFormatVersion = MessageVersion,
                    deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                    primaryPin = primaryPin,
                    secondaryPin = secondaryPin,
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    invoiceNumber = generateInvoice(terminalId, invoice),
                    customerData = customerData,
                    dealerData = null,
                    productData = listOf(
                        ProductData(
                            unitCode = "Unit",
                            productCode = productCode,
                            productUnitPrice = unitPrice,
                            productAmount = productAmount,
                            productQuantity = productQuantity,
                            productNetUnitPrice = unitPrice,
                            productNetAmount = productAmount,
                            skuCategory = "Unit"
                        )
                    )
                )
            }
        }

        fun sale(
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
            dealerData: DealerData?,
            operationType: Boolean,
        ): NativeRequest {
            return if (operationType) {
                NativeRequest(
                    processingMode = ProcessingModeHost,
                    terminalIdentification = terminalId,
                    terminalCode = terminalId,
                    systemModel = systemModel,
                    systemVersion = systemVersion,
                    transactionCode = SaleTransactionCode,
                    transactionSequenceNumber = transactionSequenceNumber.toString(),
                    entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                    currencyCode = currencyCode,
                    localTransactionDate = localDateTime.transactionDate,
                    localTransactionTime = localDateTime.transactionTime,
                    applicationType = ApplicationTypeFleetControl,
                    accountType = AccountTypeFleetTrack,
                    messageFormatVersion = MessageVersion,
                    deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                    primaryPin = primaryPin,
                    secondaryPin = secondaryPin,
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    productCode = productCode,
                    productUnitPrice = unitPrice.formattedUnitPrice,
                    productAmount = productAmount.formattedAmount,
                    productQuantity = productQuantity.formattedVolume,
                    transactionAmount = transactionAmount.formattedAmount,
                    invoiceNumber = generateInvoice(terminalId, invoice),
                    customerData = customerData,
                    dealerData = dealerData,
                )
            } else {
                NativeRequest(
                    processingMode = ProcessingModeHost,
                    terminalIdentification = terminalId,
                    terminalCode = terminalId,
                    systemModel = systemModel,
                    systemVersion = systemVersion,
                    transactionCode = SaleTransactionCode,
                    transactionSequenceNumber = transactionSequenceNumber.toString(),
                    entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                    currencyCode = currencyCode,
                    localTransactionDate = localDateTime.transactionDate,
                    localTransactionTime = localDateTime.transactionTime,
                    applicationType = ApplicationTypeFleetControl,
                    accountType = AccountTypeFleetTrack,
                    messageFormatVersion = MessageVersion,
                    deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                    primaryPin = primaryPin,
                    secondaryPin = secondaryPin,
                    primaryTrack = primaryTrack,
                    secondaryTrack = secondaryTrack,
                    invoiceNumber = generateInvoice(terminalId, invoice),
                    customerData = customerData,
                    dealerData = dealerData,
                    productData = listOf(
                        ProductData(
                            unitCode = "Unit",
                            productCode = productCode,
                            productUnitPrice = unitPrice,
                            productAmount = productAmount,
                            productQuantity = productQuantity,
                            productNetUnitPrice = unitPrice,
                            productNetAmount = productAmount,
                            skuCategory = "Unit"
                        )
                    )
                )
            }
        }

        fun gncSale(
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
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = SaleTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = CurrencyCodeARS,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                primaryPin = primaryPin,
                secondaryPin = secondaryPin,
                primaryTrack = primaryTrack,
                secondaryTrack = secondaryTrack,
                invoiceNumber = generateInvoice(terminalId, invoice),
                productData = productData,
                customerData = customerData,
                pumpNumber = pumpNumber.toString(),
            )
        }

        fun balanceInquiry(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            localDateTime: LocalDateTime,
            manualEntry: Boolean,
            primaryTrack: String,
            pumpNumber: Int,
            productCode: String,
            unitPrice: Double,
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = BalanceInquiryTransactionCode,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                entryMethod = if (manualEntry) ManualEntryMethod else SwipeEntryMethod,
                currencyCode = CurrencyCodeARS,
                localTransactionDate = localDateTime.transactionDate,
                localTransactionTime = localDateTime.transactionTime,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                primaryTrack = primaryTrack,
                pumpNumber = pumpNumber.toString(),
                productCode = productCode,
                productUnitPrice = unitPrice.formattedUnitPrice,
            )
        }

        fun voidTransaction(
            terminalId: String,
            systemModel: String,
            systemVersion: String,
            transactionSequenceNumber: Long,
            localTransactionDate: LocalDateTime,
            localTransactionTime: LocalDateTime,
            primaryTrack: String,
            originalData: OriginalData?
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = VoidTransactionCode,
                entryMethod = SwipeEntryMethod,
                applicationType = ApplicationTypeFleetControl,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                currencyCode = CurrencyCodeARS,
                deviceTypeIdentifier = DeviceTypeIdentifierOtherSelfService,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                localTransactionDate = localTransactionDate.transactionDate,
                localTransactionTime = localTransactionTime.transactionTime,
                terminalIdentification = terminalId,
                originalData = originalData,
                terminalCode = terminalId,
                primaryTrack = primaryTrack,
                authorizationCode = null,
                unitCode = null,
                pumpNumber = null,
                primaryPin = null,
                secondaryTrack = null,
                secondaryPin = null,
                transactionAmount = null,
                productCode = null,
                productUnitPrice = null,
                productAmount = null,
                productQuantity = null,
                customerData = null,
                productData = null,
                transactionNetAmount = null,
                productTaxes = null,
                invoiceNumber = null,
                serviceCode = null,
                batchNumber = null,
                transactionExtendedData = null,
                responseCode = null,
                responseText = null
            )
        }

        fun batchClose(
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
        ): NativeRequest {
            return NativeRequest(
                processingMode = ProcessingModeHost,
                terminalIdentification = terminalId,
                terminalCode = terminalId,
                systemModel = systemModel,
                systemVersion = systemVersion,
                transactionCode = BatchCloseCode,
                localDate = localTransactionDate.transactionDate,
                localTime = localTransactionTime.transactionTime,
                transactionSequenceNumber = transactionSequenceNumber.toString(),
                applicationType = ApplicationTypeFleetControl,
                currencyCode = CurrencyCodeARS,
                accountType = AccountTypeFleetTrack,
                messageFormatVersion = MessageVersion,
                deviceType = DeviceTypeIdentifierOtherSelfService,
                entryMethod = SwipeEntryMethod,
                salesCounter = salesCounter,
                salesTotal = salesTotal,
                voidedCounter = voidedCounter,
                voidedTotal = voidedTotal,
                batchNumber = batchNumber
            )
        }

        fun modules(
            terminalId: String,
            systemModel: String,
            systemVersion: String
        ): NativeRequest {
            return NativeRequest(
                subscriberCode = terminalId.take(3).uppercase(),
                transactionCode = ModulesCode,
                systemModel = systemModel,
                systemVersion = systemVersion,
                messageFormatVersion = MessageVersion,
                lastVersion = 0
            )
        }
    }
}