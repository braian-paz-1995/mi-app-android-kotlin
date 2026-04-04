package com.ationet.androidterminal.core.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.CompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.FusionCompletionEntity
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.ProductData
import com.ationet.androidterminal.core.data.local.room.entity.fleet.completion.TransactionData
import com.ationet.androidterminal.core.data.local.room.fleet.CompletionDao
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class CompletionDaoTest {
    private lateinit var completionDao: CompletionDao
    private lateinit var database: AATDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room
            .inMemoryDatabaseBuilder(context, AATDatabase::class.java)
            .build()

        completionDao = database.completionDao
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun getFusionCompletion_shouldNotThrowException() = runTest {
        createStandaloneCompletion(
            id = 1,
            authorizationCode = "1234567890",
            transactionDateTime = LocalDateTime(2025, 4, 10, 10, 30, 0),
            transactionSequenceNumber = 1,
            batchId = 1,
            primaryTrack = "C4F33",
            inputType = "1",
            name = "1234567890",
            code = "1234567890",
            unitPrice = 1.0,
            quantity = 1.0,
            amount = 1.0
        )

        createFusionCompletion(
            id = 2,
            saleId = 1234,
            authorizationCode = "1234567891",
            transactionDateTime = LocalDateTime(2025, 4, 5, 10, 30, 0),
            transactionSequenceNumber = 1,
            batchId = 1,
            primaryTrack = "C4F33",
            inputType = "1",
            name = "1234567890",
            code = "1234567890",
            unitPrice = 5.0,
            quantity = 2.0,
            amount = 10.0
        )

        val completion = completionDao.getFusionCompletion(2)

        assertNotNull(completion)
        assertEquals(2, completion.completion.id)
        assertEquals("1234567891", completion.completion.authorizationCode)
        assertEquals(LocalDateTime(2025, 4, 5, 10, 30, 0), completion.completion.transactionDateTime)
        assertEquals(1, completion.completion.transactionSequenceNumber)
        assertEquals(1, completion.completion.batchId)
        assertEquals(Configuration.ControllerType.FUSION.toString(), completion.completion.controllerType)
        assertEquals("C4F33", completion.completion.transactionData.primaryTrack)
        assertEquals("1", completion.completion.transactionData.product.inputType)
        assertEquals("1234567890", completion.completion.transactionData.product.name)
        assertEquals("1234567890", completion.completion.transactionData.product.code)
        assertEquals(5.0, completion.completion.transactionData.product.unitPrice)
        assertEquals(2.0, completion.completion.transactionData.product.quantity)
        assertEquals(10.0, completion.completion.transactionData.product.amount)
        assertEquals(1234, completion.fusion.saleId)
        assertEquals(2, completion.fusion.completionId)

    }

    private suspend fun createFusionCompletion(
        id: Int,
        saleId: Int,
        authorizationCode: String,
        transactionDateTime: LocalDateTime,
        transactionSequenceNumber: Long,
        batchId: Int,
        primaryTrack: String,
        inputType: String,
        name: String,
        code: String,
        unitPrice: Double,
        quantity: Double,
        amount: Double
    ) {
        val completion = CompletionEntity(
            id = id,
            authorizationCode = authorizationCode,
            transactionDateTime = transactionDateTime,
            transactionSequenceNumber = transactionSequenceNumber,
            batchId = batchId,
            controllerType = Configuration.ControllerType.FUSION.toString(),
            transactionData = TransactionData(
                primaryTrack = primaryTrack,
                product = ProductData(
                    inputType = inputType,
                    name = name,
                    code = code,
                    unitPrice = unitPrice,
                    quantity = quantity,
                    amount = amount
                )
            )
        )
        val fusionCompletion = FusionCompletionEntity(
            saleId = saleId,
            completionId = id
        )

        completionDao.create(completion, fusionCompletion)
    }

    private suspend fun createStandaloneCompletion(
        id: Int,
        authorizationCode: String,
        transactionDateTime: LocalDateTime,
        transactionSequenceNumber: Long,
        batchId: Int,
        primaryTrack: String,
        inputType: String,
        name: String,
        code: String,
        unitPrice: Double,
        quantity: Double,
        amount: Double
    ) {
        val completion = CompletionEntity(
            id = id,
            authorizationCode = authorizationCode,
            transactionDateTime = transactionDateTime,
            transactionSequenceNumber = transactionSequenceNumber,
            batchId = batchId,
            controllerType = Configuration.ControllerType.STAND_ALONE.toString(),
            transactionData = TransactionData(
                primaryTrack = primaryTrack,
                product = ProductData(
                    inputType = inputType,
                    name = name,
                    code = code,
                    unitPrice = unitPrice,
                    quantity = quantity,
                    amount = amount
                )
            )
        )

        completionDao.create(completion)
    }
}