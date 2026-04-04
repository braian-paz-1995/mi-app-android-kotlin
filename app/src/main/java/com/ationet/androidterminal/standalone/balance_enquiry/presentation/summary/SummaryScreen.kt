package com.ationet.androidterminal.standalone.balance_enquiry.presentation.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.ExitConfirmAlert
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.core.util.LocaleFormatter.formatNumber
import com.ationet.androidterminal.standalone.balance_enquiry.data.local.Quantity
import com.ationet.androidterminal.ui.theme.AATIcons

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onPrint: () -> Unit,
    onExit: () -> Unit
) {
    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        ExitConfirmAlert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = {
                cancelSelection = false
                onExit()
            }
        )
    }

    BackHandler {
        cancelSelection = true
    }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is SummaryState.Summary -> {
            SummaryContent(
                summaryData = state.data,
                onPrint = onPrint,
                onCancel = { cancelSelection = true },
                currencyFormat = state.data.currencyFormat,
                fuelMeasureUnit = state.data.fuelMeasureUnit,
                language = state.data.language
            )
        }
    }

}

@Composable
fun SummaryContent(
    currencyFormat: String,
    fuelMeasureUnit: String,
    language: Configuration.LanguageType,
    summaryData: SummaryData,
    onPrint: () -> Unit,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val data = mapOf(
        stringResource(R.string.label_date) to LocaleFormatter.formatDateTime(summaryData.date, context),
        stringResource(R.string.auth_code) to summaryData.authorizationCode,
        stringResource(R.string.label_product) to summaryData.productName,
        stringResource(R.string.available) to when (summaryData.quantity.inputType) {
            Quantity.InputType.Quantity -> "${formatNumber(summaryData.quantity.value.toString(), 3, language)} $fuelMeasureUnit"
            Quantity.InputType.Amount -> "$currencyFormat ${formatNumber(summaryData.quantity.value.toString(), 2, language)}"
        }
    )

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { innerPadding ->
        com.ationet.androidterminal.core.presentation.SummaryScreen(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(R.string.balance_summary),
            data = data,
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AATButtonIcon(
                        onClick = onPrint,
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(R.string.print), fontSize = 16.sp)
                                    Icon(
                                        painter = painterResource(id = AATIcons.printerIcon),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    )
                    AATTextButton(
                        onClick = onCancel,
                        text = stringResource(R.string.exit),
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}