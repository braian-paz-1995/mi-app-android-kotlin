package com.ationet.androidterminal.core.data.remote.ationet

import android.util.Log
import com.ationet.androidterminal.core.data.remote.ationet.model.DealerData
import com.ationet.androidterminal.core.data.remote.ationet.model.IdentityData
import com.ationet.androidterminal.core.data.remote.ationet.model.NativeRequest
import com.ationet.androidterminal.core.data.remote.ationet.model.OriginalData
import com.ationet.androidterminal.core.data.remote.ationet.model.ProductData
import com.ationet.androidterminal.core.domain.util.generateInvoice
import com.ationet.androidterminal.loyalty.loyalty_accumulation.data.local.AccumulationOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import java.net.InetAddress
import java.net.Socket
import java.net.SocketTimeoutException

class LocalAgentNativeInterface(
    private val ip: String,
    private val port: Int,
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

        return sendRequest(request)
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

        return sendRequest(request)
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

        return sendRequest(request)
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
        currencyCode: String
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

        return sendRequest(request)
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

        return sendRequest(request)
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
            amount = amount
        )

        return sendRequest(request)
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

        return sendRequest(request)
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
        return sendRequest(request)
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

        return sendRequest(request)
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

        return sendRequest(request)
    }
    override suspend fun requestReverseCC(
        terminalId: String,
        systemModel: String,
        systemVersion: String,
        authorizationCode: String?,
        // transactionSequenceNumber: Long,
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

        return sendRequest(request)
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

        return sendRequest(request)
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
        operationType: Boolean
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

        return sendRequest(request)
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

        return sendRequest(request)
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

        return sendRequest(request)
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
        unitPrice: Double
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

        return sendRequest(request)
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
        return sendRequest(request)
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
        return sendRequest(request)
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
        return sendRequest(request)
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
        return sendRequest(request)
    }

    private suspend fun sendRequest(request: NativeRequest): NativeRequest? {
        val socket = getSocket(ip, port)

        val response = try {
            send(socket, request)
            receive(socket)
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.e(TAG, "Local agent: request to host '$ip:$port' failed", e)

            closeSocket()
            throw e
        }

        return response
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun send(socket: Socket, request: NativeRequest) {
        val writeChannel = withContext(Dispatchers.IO) {
            socket.getOutputStream()
        }

        Log.v(TAG, "Local agent: request to host '$ip:$port'= $request")

        withContext(Dispatchers.IO) {
            jsonSerializer.encodeToStream(request, writeChannel)
            writeChannel.flush()
        }
    }

    private suspend fun receive(socket: Socket): NativeRequest? {
        val readChannel = withContext(Dispatchers.IO) {
            socket.soTimeout = 10_000
            socket.getInputStream()
        }.bufferedReader()

        val buffer = CharArray(8 * 1024)
        var readOffset = 0
        do {
            val read = withContext(Dispatchers.IO) {
                try {
                    readChannel.read(buffer, readOffset, buffer.size - readOffset)
                } catch (_: SocketTimeoutException) {
                    -1
                }
            }

            if (read == -1) {
                break
            }

            readOffset += read

            /* Set inter byte timeout */
            if (socket.soTimeout != 100) {
                socket.soTimeout = 100
            }
        } while (socket.isConnected)

        if (!socket.isConnected) {
            return null
        }

        val responseLine = buffer.slice(0 until readOffset).joinToString(separator = "")
        return if (responseLine.isNotBlank()) {
            val response = jsonSerializer.decodeFromString<NativeRequest?>(responseLine)

            Log.v(TAG, "Local agent: response from host '$ip:$port'= $response")

            response
        } else {
            Log.v(TAG, "Local agent: response from host '$ip:$port' empty")

            null
        }
    }

    private suspend fun getSocket(ip: String, port: Int): Socket {
        val localSocket = socket
        if (localSocket == null) {
            // Create socket
            val newSocket = createSocket(ip, port)

            socket = newSocket
            return newSocket
        }


        if (port != localSocket.port) {
            closeSocket()

            // Create socket
            val newSocket = createSocket(ip, port)

            socket = newSocket
            return newSocket
        }

        val remoteAddress = withContext(Dispatchers.IO) {
            localSocket.inetAddress.hostName
        }
        if (ip != remoteAddress) {
            closeSocket()

            // Create socket
            val newSocket = createSocket(ip, port)

            socket = newSocket
            return newSocket
        }

        return localSocket
    }

    private suspend fun createSocket(ip: String, port: Int): Socket {
        val socketAddress = withContext(Dispatchers.IO) {
            InetAddress.getByName(ip)
        }

        return withContext(Dispatchers.IO) {
            Socket(socketAddress, port)
        }.also {
            Log.d(TAG, "Local agent: connected to host '$ip:$port'")
        }
    }

    private suspend fun closeSocket() {
        val localSocket = socket ?: return

        Log.d(TAG, "Local agent: closing socket to host '$ip:$port'")

        try {
            withContext(Dispatchers.IO) {
                localSocket.close()
            }
        } catch (e: Throwable) {
            currentCoroutineContext().ensureActive()
            Log.d(TAG, "Local agent: failed to close tcp connection to '$ip:$port'")
        } finally {
            socket = null
        }
    }

    companion object {
        private const val TAG: String = "LocalAgentInterface"

        @OptIn(ExperimentalSerializationApi::class)
        private val jsonSerializer: Json = Json {
            isLenient = true
            allowTrailingComma = true
            coerceInputValues = true
            ignoreUnknownKeys = true
        }
        private var socket: Socket? = null
    }
}