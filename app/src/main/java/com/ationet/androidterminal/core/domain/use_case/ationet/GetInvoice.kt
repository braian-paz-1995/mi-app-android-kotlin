package com.ationet.androidterminal.core.domain.use_case.ationet

import android.content.Context
import com.ationet.androidterminal.core.data.local.util.InvoiceHelper.getNextInvoiceNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetInvoice @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend operator fun invoke(): String {
        return getNextInvoiceNumber(context).toString()
    }
}