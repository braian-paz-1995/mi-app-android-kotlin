package com.ationet.androidterminal.standalone.preauthorization.presentation.summary

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.preauthorization.InputType
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.core.presentation.SummaryScreen as SummaryScreenCore

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onPrint: () -> Unit,
    onExit: () -> Unit
) {

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is SummaryState.Summary -> {
            SummaryContent(
                summaryData = state.data,
                onPrint = onPrint,
                onExit = onExit
            )
        }
    }

}

@Composable
fun SummaryContent(
    summaryData: SummaryData,
    onPrint: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { }

    val unitRequestedLabel = if (summaryData.selectedInputType == InputType.Amount) {
        R.string.label_amount_requested
    } else if (summaryData.selectedInputType == InputType.Quantity){
        R.string.label_quantity_requested
    } else
        R.string.ationet

    val unitRequestedValue = if (summaryData.selectedInputType == InputType.Amount) {
        "${summaryData.currency} ${summaryData.amountRequested}"
    } else if (summaryData.selectedInputType == InputType.Quantity){
        "${summaryData.quantityRequested} ${summaryData.fuelMeasureUnit}"
    }else
        ""

    val data = buildMap {
        put(
            stringResource(R.string.label_date),
            LocaleFormatter.formatDate(summaryData.date, LocalContext.current)
        )
        put(stringResource(R.string.label_auth_code), summaryData.authCode)
        put(stringResource(R.string.label_product), summaryData.product)

        if (unitRequestedValue != "${
                LocaleFormatter.formatNumber(
                    "0",
                    3,
                    summaryData.language
                )
            } L" ) {

            if (!unitRequestedValue.isNullOrBlank()) /* it means it's a fill-up and there isn't a requested value */
                put(stringResource(unitRequestedLabel), unitRequestedValue)
        }

        if (summaryData.selectedInputType == InputType.Amount && summaryData.amountAuthorized!= null)
            put(stringResource(R.string.label_amount_authorized), "${(summaryData.currency)} ${(summaryData.amountAuthorized)}") else if (summaryData.selectedInputType == InputType.Quantity && summaryData.quantityAuthorized!= null)
            put(stringResource(R.string.label_quantity_authorized), "${summaryData.quantityAuthorized} ${summaryData.fuelMeasureUnit}") else
                if (summaryData.selectedInputType == InputType.FillUp){
                    if (summaryData.quantityAuthorized!=null)
                        put(stringResource(R.string.label_quantity_authorized), "${summaryData.quantityAuthorized} ${summaryData.fuelMeasureUnit}")
                    if (summaryData.amountAuthorized!=null)
                        put(stringResource(R.string.label_amount_authorized), "${(summaryData.currency)} ${(summaryData.amountAuthorized)}")
                }
                else
                    ""
        /*put(
            stringResource(R.string.label_quantity_authorized),
            "${summaryData.quantityAuthorized} ${summaryData.fuelMeasureUnit}"
        )*/
    }

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { innerPadding ->
        SummaryScreenCore(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 10.dp, bottom = 6.dp),
            title = stringResource(R.string.authorization_summary),
            data = data,
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                        onClick = { cancelSelection = true },
                        text = stringResource(R.string.exit),
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}