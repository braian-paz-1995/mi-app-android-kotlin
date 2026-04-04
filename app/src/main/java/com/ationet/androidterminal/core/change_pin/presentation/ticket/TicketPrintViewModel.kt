package com.ationet.androidterminal.core.change_pin.presentation.ticket

import android.content.Context
import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.change_pin.data.local.ChangePinOperationStateRepository
import com.ationet.androidterminal.core.data.local.util.splitWordLimit
import com.ationet.androidterminal.core.data.remote.ationet.ResponseCodes
import com.ationet.androidterminal.core.domain.model.receipt.Receipt
import com.ationet.androidterminal.core.domain.util.getTransactionTypeName
import com.ationet.androidterminal.core.util.LocaleFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class TicketUiModel(
    val header: List<String>,
    val transactionHeader: Triple<String, String, String>,
    val siteInfo: List<String>,
    val transactionInfo: List<String>,
    val ationetData: List<String>,
    val resultMessage: List<String>,
    val footer: List<String>,
    val isSuccess: Boolean
)
@HiltViewModel
class TicketViewModel @Inject constructor(
    private val mapper: TicketReceiptMapper,
    private val repository: ChangePinOperationStateRepository
) : ViewModel() {

    private val _uiModel = MutableStateFlow<TicketUiModel?>(null)
    val uiModel: StateFlow<TicketUiModel?> = _uiModel

    init {
        val receipt = repository.getState().receipt
        receipt?.let {
            _uiModel.value = mapper.map(it)
        }
    }
}
class TicketReceiptMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun map(receipt: Receipt): TicketUiModel {

        val header = receipt.header.title.splitWordLimit(21) +
                receipt.header.subtitle.splitWordLimit(42)

        val transactionName =
            "(" + getTransactionTypeName(context, receipt.transactionLine.name) + ")"

        val date = LocaleFormatter.formatDate(receipt.transactionLine.dateTime, context)
        val time = LocaleFormatter.formatTime(receipt.transactionLine.dateTime, context)

        val transactionHeader = Triple(date, transactionName, time)

        val siteInfo = listOf(
            "${receipt.site.code} - ${receipt.site.name}",
            receipt.site.address,
            receipt.site.cuit
        )

        val isSuccess =
            receipt.transactionData.responseCode == ResponseCodes.AUTHORIZED

        val transactionInfo = mutableListOf<String>()

        transactionInfo.add(
            context.getString(
                R.string.receipt_terminal_id,
                receipt.transactionData.terminalId
            )
        )

        receipt.transactionData.authorizationCode?.let {
            transactionInfo.add(context.getString(R.string.receipt_authorization_code))
            transactionInfo.add(it)
        }

        val resultMessage = if (isSuccess) {
            context.getString(R.string.receipt_successful_change_pin)
                .splitWordLimit(21)
        } else {
            receipt.transactionData.responseText.splitWordLimit(21)
        }

        val footer = listOf(
            receipt.footer.footer,
            receipt.footer.bottomNote
        )

        return TicketUiModel(
            header = header,
            transactionHeader = transactionHeader,
            siteInfo = siteInfo,
            transactionInfo = transactionInfo,
            ationetData = emptyList(),
            resultMessage = resultMessage,
            footer = footer,
            isSuccess = isSuccess
        )
    }
}
