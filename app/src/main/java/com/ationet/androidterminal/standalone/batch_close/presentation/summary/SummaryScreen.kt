package com.ationet.androidterminal.standalone.batch_close.presentation.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.DarkGrayAlternate
import com.ationet.androidterminal.ui.theme.Gray

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onPrint: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { }

    var cancelSelection by remember { mutableStateOf(false) }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = {
                cancelSelection = false
                onExit()
            }
        )
    }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is SummaryState.Summary -> {
            SummaryContent(
                summaryData = state.data,
                onPrint = onPrint,
                onCancel = { cancelSelection = true },
                currencyFormat = viewModel.currencyFormat
            )
        }
    }
}

@Composable
fun SummaryContent(
    summaryData: SummaryData,
    currencyFormat: String,
    onPrint: () -> Unit,
    onCancel: () -> Unit,
) {
    val data = mapOf(
        stringResource(R.string.batch_id) to summaryData.batchId,
        stringResource(R.string.sales_count) to summaryData.salesCount,
        stringResource(R.string.sales_amount) to "$currencyFormat ${summaryData.salesTotal}",
        stringResource(R.string.cancelled_sales) to summaryData.cancelledSalesCount,
        stringResource(R.string.cancelled_amount) to "$currencyFormat ${summaryData.cancelledSalesTotal}",
        stringResource(R.string.rechargecc_count) to summaryData.rechargeCCCount,
        stringResource(R.string.rechargecc_amount) to "$currencyFormat ${summaryData.rechargeCCTotal}",
        stringResource(R.string.reversecc_count) to summaryData.reverseCCCount,
        stringResource(R.string.reversecc_amount) to "$currencyFormat ${summaryData.reverseCCTotal}",
    )
    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { innerPadding ->
        SummaryScreen(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(R.string.batch_close_summary),
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

@Composable
fun SummaryScreen(
    modifier: Modifier,
    title: String,
    data: Map<String, String>,
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    prominentStyle: TextStyle? = null,
    buttons: @Composable BoxScope.() -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val isFirstVisibleItem = remember {
        derivedStateOf {
            listState.canScrollForward
        }
    }

    Surface {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp),
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                icon?.invoke(this)


                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f) //
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    items(data.entries.toList()) { (key, value) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = key,
                                    fontSize = 18.sp,
                                    color = DarkGrayAlternate
                                )

                                val boldKeys = setOf(
                                    stringResource(R.string.amount),
                                    stringResource(R.string.quantity),
                                    stringResource(R.string.authorized_amount),
                                    stringResource(R.string.authorized_quantity),
                                    stringResource(R.string.amount_completed),
                                    stringResource(R.string.quantity_completed),
                                    stringResource(R.string.amount_selected),
                                    stringResource(R.string.quantity_selected),
                                    stringResource(R.string.label_amount_requested),
                                    stringResource(R.string.label_amount_authorized),
                                    stringResource(R.string.label_quantity_authorized),
                                    stringResource(R.string.label_quantity_requested)
                                )

                                val useBold = key in boldKeys

                                Text(
                                    text = value,
                                    fontWeight = if (useBold) FontWeight.Bold else FontWeight.Normal,
                                    style = if (useBold && prominentStyle != null)
                                        prominentStyle
                                    else
                                        TextStyle(
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        ),
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(
                                color = Gray.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .background(color = Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    buttons.invoke(this@Box)
                }
            }
        }
    }
}
