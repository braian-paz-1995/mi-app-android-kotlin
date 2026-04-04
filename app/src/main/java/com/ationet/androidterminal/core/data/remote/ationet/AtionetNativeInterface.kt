package com.ationet.androidterminal.core.data.remote.ationet

import android.util.Log
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.IdentityData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.data.remote.ationet.model.ProductData
import com.ationet.androidterminal.core.domain.util.generateInvoice
import com.ationet.androidterminal.loyalty.loyalty_accumulation.data.local.AccumulationOption
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.json.Json

class AtionetNativeInterface(
    private val hostUrl: String,
) : NativeInterface {
    override suspend fun requestPreAuthorization(
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
    ): NativeRequest? {
        val request = NativeRequest.preAuthorization(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            authorizationCode = authorizationCode,
            pumpNumber = pumpNumber,
            transactionSequenceNumber = transactionSequenceNumber,
            productCode = productCode,
            unitPrice = unitPrice,
            productAmount = productAmount,
            productQuantity = productQuantity,
            primaryTrack = primaryTrack,
            primaryPin = primaryPin,
            secondaryTrack = secondaryTrack,
            secondaryPin = secondaryPin,
            invoice = invoice,
            manualEntry = manualEntry,
            customerData = customerData,
            localDateTime = localDateTime,
            unitCode = unitCode,
            currencyCode = currencyCode,
            dealerData = dealerData
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestCompletion(
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
        primaryPin: String?,
        secondaryTrack: String?,
        secondaryPin: String?,
        manualEntry: Boolean,
        customerData: Map<String, String?>?,
        localDateTime: LocalDateTime,
        originalData: OriginalData?,
        currencyCode: String,
        dealerData: DealerData?
    ): NativeRequest? {
        val request = NativeRequest.completion(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            authorizationCode = authorizationCode,
            transactionSequenceNumber = transactionSequenceNumber,
            pumpNumber = pumpNumber,
            productCode = productCode,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitPrice = unitPrice,
            invoice = invoice,
            primaryTrack = primaryTrack,
            primaryPin = primaryPin,
            secondaryTrack = secondaryTrack,
            secondaryPin = secondaryPin,
            manualEntry = manualEntry,
            customerData = customerData,
            localDateTime = localDateTime,
            originalData = originalData,
            currencyCode = currencyCode,
            dealerData = dealerData
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestContingency(
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
        customerData: Map<String, String?>?,
        localDateTime: LocalDateTime
    ): NativeRequest? {
        val request = NativeRequest.contingency(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            authorizationCode = authorizationCode,
            transactionSequenceNumber = transactionSequenceNumber,
            pumpNumber = pumpNumber,
            productCode = productCode,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitPrice = unitPrice,
            invoice = invoice,
            primaryTrack = primaryTrack,
            manualEntry = manualEntry,
            customerData = customerData,
            localDateTime = localDateTime,
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestCancellation(
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
        currencyCode: String,
    ): NativeRequest? {
        val request = NativeRequest.cancellation(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            authorizationCode = authorizationCode,
            transactionSequenceNumber = transactionSequenceNumber,
            localDateTime = localDateTime,
            manualEntry = manualEntry,
            originalAuthorizationCode = originalAuthorizationCode,
            originalTransactionSequenceNumber = originalTransactionSequenceNumber,
            originalLocalDateTime = originalLocalDateTime,
            currencyCode = currencyCode,
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestChangePin(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String?,
        transactionSequenceNumber: Long,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        primaryPin: String,
        oldPin: String,
        newPin: String,
        confirmationPin: String
    ): NativeRequest? {
        val request = NativeRequest.pinChange(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            track = track,
            primaryPin = primaryPin,
            oldPin = oldPin,
            newPin = newPin,
            confirmationPin = confirmationPin
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestRechargeCC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String?,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String?,
        password: String?
    ): NativeRequest? {
        val request = NativeRequest.rechargeCC(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            track = track,
            amount = amount,
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/consumercard",
            username = username,
            password = password
        )
    }

    override suspend fun requestLoyaltyAccumulation(
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
        username: String?,
        password: String?,
        paymentMethod: String?,
        optionType: AccumulationOption?,
        identityData: IdentityData?
    ): NativeRequest? {
        val request = NativeRequest.loyaltyAccumulation(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            primaryTrack = primaryTrack,
            productCode = productCode,
            productUnitPrice = productUnitPrice,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitCode = unitCode,
            currencyCode = currencyCode,
            paymentMethod = paymentMethod,
            optionType = optionType,
            identityData = identityData
        )
        if (optionType == AccumulationOption.POINTS) {
            return sendRequest(
                hostUrl,
                request,
                path = "v1/loyalty",
                username = username,
                password = password
            )
        } else {
            return sendRequest(
                hostUrl,
                request,
                path = "v1/Community",
                username = username,
                password = password
            )
        }
    }

    override suspend fun requestLoyaltyBalanceEnquiry(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        primaryTrack: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        currencyCode: String,
        username: String?,
        password: String?
    ): NativeRequest? {
        val request = NativeRequest.loyaltyBalanceEnquiry(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            primaryTrack = primaryTrack,
            currencyCode = currencyCode
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/loyalty",
            username = username,
            password = password
        )
    }

    override suspend fun requestLoyaltyPointsRedemption(
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
        username: String?,
        password: String?,
        paymentMethod: String?
    ): NativeRequest? {
        val request = NativeRequest.loyaltyPointsRedemption(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            primaryTrack = primaryTrack,
            productCode = productCode,
            productUnitPrice = productUnitPrice,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitCode = unitCode,
            currencyCode = currencyCode,
            paymentMethod = paymentMethod
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/loyalty",
            username = username,
            password = password
        )
    }

    override suspend fun requestLoyaltyRewardsRedemption(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        productCode: String,
        productUnitPrice: Double,
        productAmount: Double?,
        productQuantity: Double?,
        withdrawalCode: String,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        unitCode: String,
        currencyCode: String,
        username: String?,
        password: String?
    ): NativeRequest? {
        val request = NativeRequest.loyaltyRewardsRedemption(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            withdrawalCode = withdrawalCode,
            productAmount = productAmount,
            currencyCode = currencyCode
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/loyalty",
            username = username,
            password = password
        )
    }

    override suspend fun requestReverseCC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String?,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String?,
        password: String?,
        currencyCode: String

    ): NativeRequest? {
        val request = NativeRequest.reverseCC(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            track = track,
            currencyCode = currencyCode
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/consumercard",
            username = username,
            password = password
        )
    }

    override suspend fun requestActiveGC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String?,
        manualEntry: Boolean,
        localDateTime: LocalDateTime,
        track: String,
        amount: Double,
        username: String?,
        password: String?,
        currencyCode: String

    ): NativeRequest? {
        val request = NativeRequest.activeGC(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            track = track,
            currencyCode = currencyCode
        )
        return sendRequest(
            hostUrl,
            request,
            path = "v1/giftcard",
            username = username,
            password = password
        )
    }

    override suspend fun requestPostpaidSale(
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
    ): NativeRequest? {
        val request = NativeRequest.postPaidSale(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            authorizationCode = authorizationCode,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            pumpNumber = pumpNumber,
            productCode = productCode,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitPrice = unitPrice,
            transactionAmount = transactionAmount,
            primaryTrack = primaryTrack,
            primaryPin = primaryPin,
            secondaryPin = secondaryPin,
            secondaryTrack = secondaryTrack,
            invoice = invoice,
            customerData = customerData,
            currencyCode = currencyCode,
            operationType = operationType
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestSale(
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
    ): NativeRequest? {

        val request = NativeRequest.sale(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            primaryPin = primaryPin,
            secondaryPin = secondaryPin,
            primaryTrack = primaryTrack,
            secondaryTrack = secondaryTrack,
            invoice = invoice,
            productCode = productCode,
            productAmount = productAmount,
            productQuantity = productQuantity,
            unitPrice = unitPrice,
            transactionAmount = transactionAmount,
            customerData = customerData,
            currencyCode = currencyCode,
            dealerData = dealerData,
            operationType = operationType
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestGncSale(
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
    ): NativeRequest? {
        val request = NativeRequest.gncSale(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            manualEntry = manualEntry,
            localDateTime = localDateTime,
            pumpNumber = pumpNumber,
            productData = productData,
            primaryTrack = primaryTrack,
            primaryPin = primaryPin,
            secondaryPin = secondaryPin,
            secondaryTrack = secondaryTrack,
            invoice = generateInvoice(terminalId, invoice),
            customerData = customerData,
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestBalanceInquiry(
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
    ): NativeRequest? {
        val request = NativeRequest.balanceInquiry(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            localDateTime = localDateTime,
            manualEntry = manualEntry,
            primaryTrack = primaryTrack,
            pumpNumber = pumpNumber,
            productCode = productCode,
            unitPrice = unitPrice,
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestVoidTransaction(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        primaryTrack: String,
        originalData: OriginalData?
    ): NativeRequest? {
        val request = NativeRequest.voidTransaction(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            localTransactionDate = localTransactionDate,
            localTransactionTime = localTransactionTime,
            primaryTrack = primaryTrack,
            originalData = originalData
        )

        return sendRequest(hostUrl, request)
    }

    override suspend fun requestLoyaltyVoidTransaction(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        transactionSequenceNumber: Long,
        localTransactionDate: LocalDateTime,
        localTransactionTime: LocalDateTime,
        primaryTrack: String,
        originalData: OriginalData?
    ): NativeRequest? {
        val request = NativeRequest.loyaltyVoidTransaction(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            localTransactionDate = localTransactionDate,
            localTransactionTime = localTransactionTime,
            primaryTrack = primaryTrack,
            originalData = originalData
        )

        return sendRequest(hostUrl, request, path = "v1/loyalty")
    }

    override suspend fun requestBatchClose(
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
    ): NativeRequest? {
        val request = NativeRequest.batchClose(
            terminalId = terminalId,
            systemModel = systemModel,
            systemVersion = systemVersion,
            transactionSequenceNumber = transactionSequenceNumber,
            localTransactionDate = localTransactionDate,
            localTransactionTime = localTransactionTime,
            salesCounter = salesCounter,
            salesTotal = salesTotal,
            voidedCounter = voidedCounter,
            voidedTotal = voidedTotal,
            batchNumber = batchNumber
        )
        return sendRequest(hostUrl, request, path = "v1/admin")
    }

    override suspend fun requestModules(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        messageFormatVersion: String,
        subscriberCode: String,
        lastVersion: Int
    ): NativeRequest? {
        val request = NativeRequest.modules(
            systemModel = systemModel,
            systemVersion = systemVersion,
            terminalId = terminalId
        )
        return sendRequest(hostUrl, request, path = "v1/admin")
    }

    private suspend fun sendRequest(
        hostUrl: String,
        request: NativeRequest,
        path: String = "v1/auth",
        username: String? = null,
        password: String? = null
    ): NativeRequest? {
        val response = client.post {
            url {
                takeFrom(hostUrl)
                path(path)
            }
            if (!username.isNullOrBlank() && !password.isNullOrBlank()) {

                val maskedPassword = if (password.length > 4) {
                    "*".repeat(password.length - 4) + password.takeLast(4)
                } else {
                    password
                }
                Log.d(TAG, "Authorization user='$username' password='$maskedPassword'")

                val credentials = "$username:$password"
                headers {
                    append("Authorization", "Basic $credentials")
                }
            }

            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(request)
        }

        return response.body()
    }

    companion object {
        private const val TAG: String = "NativeInterface"

        private val client: HttpClient by lazy {
            HttpClient(Android) {
                install(ContentNegotiation) {
                    expectSuccess = false
                    json(
                        json = Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                    )
                    json(
                        json = Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                        },
                        contentType = ContentType.Text.Plain
                    )
                }


                install(Logging) {
                    level = LogLevel.BODY
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d(TAG, message)
                        }
                    }
                }

                engine {
                    connectTimeout = 10_000
                    socketTimeout = 50_000
                }
            }
        }
    }
}