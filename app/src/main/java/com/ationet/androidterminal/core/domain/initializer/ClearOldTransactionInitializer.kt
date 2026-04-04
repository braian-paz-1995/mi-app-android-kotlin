package com.ationet.androidterminal.core.domain.initializer

import android.content.Context
import androidx.startup.Initializer
import com.atio.log.Logger
import com.atio.log.util.info
import com.ationet.androidterminal.core.domain.worker.ClearOldTransactionsWorker

class ClearOldTransactionInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        Logger.info("ClearOldTransactionInitializer", "Initializing clear old transactions")
        ClearOldTransactionsWorker.enqueue(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}