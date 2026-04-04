
package com.ationet.androidterminal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ationet.androidterminal.core.data.local.room.AATDatabase
import com.ationet.androidterminal.core.data.local.room.FusionPumpLockDao
import com.ationet.androidterminal.core.domain.repository.FusionPumpLockRepository
import com.ationet.androidterminal.fusion.core.domain.model.PumpLockStatus
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class FusionPumpLockRepositoryTest {

    private lateinit var database: AATDatabase
    private lateinit var dao: FusionPumpLockDao
    private lateinit var repository: FusionPumpLockRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AATDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.fusionPumpLockDao

        repository = FusionPumpLockRepositoryImpl(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun lock_then_unlock_should_update_status() = runBlocking {

        val saleId = 101
        val terminalId = "TERM01"
        val countBefore = dao.getByFusionSaleId(saleId)
        println("Before insert: $countBefore")
        // 🔒 LOCK
        repository.updateStatus(
            fusionSaleId = saleId,
            terminalId = terminalId,
            status = PumpLockStatus.LOCKED
        )
        val all = dao.getAll()
        println("All rows: $all")
        var entity = dao.getByFusionSaleId(saleId)

        assertNotNull(entity)
        assertEquals(PumpLockStatus.LOCKED, entity?.status)
        val countBefore1 = dao.getByFusionSaleId(saleId)
        println("Before insert: $countBefore1")
        // 🔓 UNLOCK
        repository.updateStatus(
            fusionSaleId = saleId,
            terminalId = terminalId,
            status = PumpLockStatus.UNLOCKED
        )
        val all1 = dao.getAll()
        println("All rows: $all1")

        entity = dao.getByFusionSaleId(saleId)

        assertEquals(PumpLockStatus.UNLOCKED, entity?.status)
    }
    @Test
    fun getLocked_should_return_only_locked_sales() = runBlocking {

        val terminalId = "TERM01"

        // Insertamos varias ventas
        repository.updateStatus(1, terminalId, PumpLockStatus.LOCKED)
        repository.updateStatus(2, terminalId, PumpLockStatus.UNLOCKED)
        repository.updateStatus(3, terminalId, PumpLockStatus.LOCKED)
        repository.updateStatus(4, terminalId, PumpLockStatus.UNLOCKED)
        repository.updateStatus(5, terminalId, PumpLockStatus.LOCKED)

        // Ejecutamos el método nuevo
        val lockedSales = repository.getLocked()

        println("Locked sales: $lockedSales")

        // Validaciones
        assertEquals(3, lockedSales.size)

        assertTrue(lockedSales.all { it.status == PumpLockStatus.LOCKED })

        assertTrue(lockedSales.any { it.fusionSaleId == 1 })
        assertTrue(lockedSales.any { it.fusionSaleId == 3 })
        assertTrue(lockedSales.any { it.fusionSaleId == 5 })
        val deleteUnlockSales = repository.deleteUnlock()

        println("Found $deleteUnlockSales locked sales")
    }
    @Test

    fun getLocked_when_no_locked_should_return_empty_list() = runBlocking {

        val terminalId = "TERM01"

        repository.updateStatus(10, terminalId, PumpLockStatus.UNLOCKED)
        repository.updateStatus(11, terminalId, PumpLockStatus.UNLOCKED)

        val lockedSales = repository.getLocked()

        assertTrue(lockedSales.isEmpty())
    }
}