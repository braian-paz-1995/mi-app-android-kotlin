package com.ationet.androidterminal.core.presentation.receipts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.data.remote.ationet.model.ResponseCodes
import com.ationet.androidterminal.core.domain.model.receipt.ReceiptTransactionTypeName
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.core.util.minus
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.DarkGrayAlternate
import com.ationet.androidterminal.ui.theme.Gray
import com.ationet.androidterminal.ui.theme.Green
import com.ationet.androidterminal.ui.theme.LightGreenAlternate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@Composable
fun ReceiptsScreen(
    transactions: LazyPagingItems<ReceiptsListItem>,
    modifier: Modifier = Modifier,
    onReceiptClick: (Int) -> Unit,
    transactionState: Boolean = false,
    title: String = stringResource(R.string.available_receipts)
) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                AATHeaderTitle(title)
                TransactionList(
                    transactions = transactions,
                    onReceiptClick = onReceiptClick,
                    transactionState = transactionState
                )
            }
        }
    }
}


@Composable
private fun TransactionList(
    transactions: LazyPagingItems<ReceiptsListItem>,
    onReceiptClick: (Int) -> Unit,
    transactionState: Boolean
) {
    val todayDate =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(transactions.itemCount) { index ->
            val item = transactions[index]
            when (item) {
                is ReceiptsListItem.Item -> {
                    val transactionName = if (!transactionState) {
                        when (item.typeOfOperation) {
                            ReceiptTransactionTypeName.PreAuthorization -> stringResource(R.string.pre_authorization)
                            ReceiptTransactionTypeName.Completion -> stringResource(R.string.completion)
                            ReceiptTransactionTypeName.Sale -> stringResource(R.string.sale)
                            ReceiptTransactionTypeName.VoidTransaction -> stringResource(R.string.void_transaction)
                            ReceiptTransactionTypeName.ClearPending -> stringResource(R.string.clear_pending_transactions)
                            ReceiptTransactionTypeName.BalanceEnquiry -> stringResource(R.string.balance_enquiry)
                            ReceiptTransactionTypeName.BatchClose -> stringResource(R.string.batch_close)
                            ReceiptTransactionTypeName.ChangePin -> stringResource(R.string.change_pin)
                            ReceiptTransactionTypeName.RechargeCC -> stringResource(R.string.recharge_cc)
                            ReceiptTransactionTypeName.ReverseCC -> stringResource(R.string.reverse_cc)
                            ReceiptTransactionTypeName.ActiveGC -> stringResource(R.string.active_gc)
                            ReceiptTransactionTypeName.LoyaltyAccumulation -> stringResource(R.string.loyalty_accumulation)
                            ReceiptTransactionTypeName.LoyaltyDiscounts -> stringResource(R.string.loyalty_discounts)
                            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> stringResource(R.string.loyalty_balance_enquiry)
                            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> stringResource(R.string.loyalty_points_redemption)
                            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> stringResource(R.string.loyalty_rewards_redemption)
                            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> stringResource(R.string.loyalty_void_accumulation)
                        }
                    } else {
                        when (item.responseCode) {
                            ResponseCodes.Authorized -> stringResource(R.string.completed)
                            else -> stringResource(R.string.rejected)
                        }
                    }

                    val backgroundColor = if (!transactionState) {
                        when (item.typeOfOperation) {
                            ReceiptTransactionTypeName.PreAuthorization -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.Completion -> if (item.responseCode != ResponseCodes.Authorized) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.Sale -> if (item.responseCode != ResponseCodes.Authorized) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ReceiptTransactionTypeName.VoidTransaction -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ClearPending -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.BalanceEnquiry -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.BatchClose -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ChangePin -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.RechargeCC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ReverseCC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ActiveGC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyAccumulation -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyDiscounts -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> MaterialTheme.colorScheme.secondary
                        }
                    } else {
                        when (item.responseCode) {
                            ResponseCodes.Authorized -> LightGreenAlternate
                            else -> MaterialTheme.colorScheme.error
                        }
                    }

                    TransactionCard(
                        transactionName = transactionName.uppercase(),
                        backgroundColor = backgroundColor,
                        localTransactionDateTime = item.localTransactionDateTime,
                        authCode = item.authCode,
                        vehicle = item.vehicle,
                        driver = item.driver,
                        amount = item.amount,
                        quantity = item.quantity,
                        currencySymbol = item.currencySymbol,
                        unitOfMeasure = item.unitOfMeasure,
                        responseCode = item.responseCode,
                        responseMessage = item.responseMessage,
                        operation = transactionName,
                        onClick = {
                            onReceiptClick.invoke(item.receiptId)
                        }
                    )
                }

                is ReceiptsListItem.Separator -> {
                    val textSeparator = when (item.dateTime.date) {
                        todayDate -> {
                            stringResource(R.string.today)
                        }

                        todayDate.minus(1, DateTimeUnit.DAY) -> {
                            stringResource(R.string.yesterday)
                        }

                        else -> {
                            LocaleFormatter.formatDate(item.dateTime, context)
                        }
                    }

                    Text(
                        text = textSeparator,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                null -> {}
            }
        }
    }
}

@Composable
fun ReceiptsScreen(
    transactions: LazyPagingItems<ReceiptsListItem>,
    modifier: Modifier = Modifier,
    onReceiptClick: (Int, Boolean) -> Unit,
    transactionState: Boolean = false,
    title: String = stringResource(R.string.available_receipts)
) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                AATHeaderTitle(title)
                TransactionList(
                    transactions = transactions,
                    onReceiptClick = onReceiptClick,
                    transactionState = transactionState
                )
            }
        }
    }
}


@Composable
private fun TransactionList(
    transactions: LazyPagingItems<ReceiptsListItem>,
    onReceiptClick: (Int, Boolean) -> Unit,
    transactionState: Boolean
) {
    val todayDate =
        remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(transactions.itemCount) { index ->
            val item = transactions[index]
            when (item) {
                is ReceiptsListItem.Item -> {
                    val transactionName = if (!transactionState) {
                        when (item.typeOfOperation) {
                            ReceiptTransactionTypeName.PreAuthorization -> stringResource(R.string.pre_authorization)
                            ReceiptTransactionTypeName.Completion -> stringResource(R.string.completion)
                            ReceiptTransactionTypeName.Sale -> stringResource(R.string.sale)
                            ReceiptTransactionTypeName.VoidTransaction -> stringResource(R.string.void_transaction)
                            ReceiptTransactionTypeName.ClearPending -> stringResource(R.string.clear_pending_transactions)
                            ReceiptTransactionTypeName.BalanceEnquiry -> stringResource(R.string.balance_enquiry)
                            ReceiptTransactionTypeName.BatchClose -> stringResource(R.string.batch_close)
                            ReceiptTransactionTypeName.ChangePin -> stringResource(R.string.change_pin)
                            ReceiptTransactionTypeName.RechargeCC -> stringResource(R.string.recharge_cc)
                            ReceiptTransactionTypeName.ReverseCC -> stringResource(R.string.reverse_cc)
                            ReceiptTransactionTypeName.ActiveGC -> stringResource(R.string.active_gc)
                            ReceiptTransactionTypeName.LoyaltyAccumulation -> stringResource(R.string.loyalty_accumulation)
                            ReceiptTransactionTypeName.LoyaltyDiscounts -> stringResource(R.string.loyalty_discounts)
                            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> stringResource(R.string.loyalty_balance_enquiry)
                            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> stringResource(R.string.loyalty_points_redemption)
                            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> stringResource(R.string.loyalty_rewards_redemption)
                            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> stringResource(R.string.loyalty_void_accumulation)
                        }
                    } else {
                        when (item.responseCode) {
                            ResponseCodes.Authorized -> stringResource(R.string.completed)
                            else -> stringResource(R.string.rejected)
                        }
                    }

                    val backgroundColor = if (!transactionState) {
                        when (item.typeOfOperation) {
                            ReceiptTransactionTypeName.PreAuthorization -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.Completion -> if (item.responseCode != ResponseCodes.Authorized) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.Sale -> if (item.responseCode != ResponseCodes.Authorized) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ReceiptTransactionTypeName.VoidTransaction -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ClearPending -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.BalanceEnquiry -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.BatchClose -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ChangePin -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.RechargeCC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ReverseCC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.ActiveGC -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyAccumulation -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyDiscounts -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyBalanceEnquiry -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyPointsRedemption -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyRewardsRedemption -> MaterialTheme.colorScheme.secondary
                            ReceiptTransactionTypeName.LoyaltyVoidTransaction -> MaterialTheme.colorScheme.secondary
                        }
                    } else {
                        when (item.responseCode) {
                            ResponseCodes.Authorized -> LightGreenAlternate
                            else -> MaterialTheme.colorScheme.error
                        }
                    }

                    TransactionCard(
                        transactionName = transactionName.uppercase(),
                        backgroundColor = backgroundColor,
                        localTransactionDateTime = item.localTransactionDateTime,
                        authCode = item.authCode,
                        vehicle = item.vehicle,
                        driver = item.driver,
                        amount = item.amount,
                        quantity = item.quantity,
                        currencySymbol = item.currencySymbol,
                        unitOfMeasure = item.unitOfMeasure,
                        responseCode = item.responseCode,
                        responseMessage = item.responseMessage,
                        operation = transactionName,
                        onClick = {
                            onReceiptClick.invoke(item.receiptId, item.copy)
                        }
                    )
                }

                is ReceiptsListItem.Separator -> {
                    val textSeparator = when (item.dateTime.date) {
                        todayDate -> {
                            stringResource(R.string.today)
                        }

                        todayDate.minus(1, DateTimeUnit.DAY) -> {
                            stringResource(R.string.yesterday)
                        }

                        else -> {
                            LocaleFormatter.formatDate(item.dateTime, context)
                        }
                    }

                    Text(
                        text = textSeparator,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                null -> {}
            }
        }
    }
}

@Composable
private fun TransactionCard(
    transactionName: String,
    backgroundColor: Color,
    localTransactionDateTime: LocalDateTime,
    authCode: String,
    vehicle: String?,
    driver: String?,
    amount: Double?,
    quantity: Double?,
    unitOfMeasure: String,
    currencySymbol: String,
    responseCode: String? = null,
    responseMessage: String? = null,
    operation: String? = null,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .shadow(4.dp, shape = RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(
                    status = transactionName,
                    backgroundColor = backgroundColor
                )

                Text(
                    text = LocaleFormatter.formatDateTime(
                        dateTime = localTransactionDateTime,
                        context = LocalContext.current,
                        showSeconds = false
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            /* Authorization code */
            TransactionDetail(
                label = stringResource(R.string.auth_code),
                value = authCode
            )

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 6.dp),
                thickness = 0.5.dp,
                color = Gray
            )

            if (responseCode != ResponseCodes.Authorized) {
                TransactionDetail(
                    label = stringResource(R.string.card_error_code),
                    value = responseCode.orEmpty()
                )
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 6.dp),
                    thickness = 0.5.dp,
                    color = Gray
                )
                TransactionDetail(
                    label = stringResource(R.string.card_error_message),
                    value = if (responseMessage.orEmpty().length > 30) responseMessage?.substring(
                        0,
                        30
                    ) + "..." else responseMessage.orEmpty(),
                    MaterialTheme
                        .typography
                        .bodyLarge
                        .copy(
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.End
                        )
                )
            } else {
                if (!vehicle.isNullOrBlank()) {
                    TransactionDetail(
                        label = stringResource(R.string.vehicle),
                        value = vehicle
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 6.dp),
                        thickness = 0.5.dp,
                        color = Gray
                    )
                }

                if (operation != null) {
                    TransactionDetail(
                        label = stringResource(R.string.operation),
                        value = operation.toString()
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 6.dp),
                        thickness = 0.5.dp,
                        color = Gray
                    )
                }

                if (!driver.isNullOrBlank()) {
                    TransactionDetail(
                        label = stringResource(R.string.driver),
                        value = driver
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 6.dp),
                        thickness = 0.5.dp,
                        color = Gray
                    )
                }

                if (quantity != null) {
                    TransactionDetail(
                        label = stringResource(R.string.quantity),
                        value = LocaleFormatter.formatNumber(
                            quantity.toString(),
                            3,
                            LocalContext.current
                        ) + " " + unitOfMeasure,
                        style = MaterialTheme.typography.titleLarge.copy(color = Green)
                    )

                    /* Not the last item to show */
                    if (amount != null) {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 6.dp),
                            thickness = 0.5.dp,
                            color = Gray
                        )
                    }
                }

                if (amount != null) {
                    TransactionDetail(
                        label = stringResource(R.string.amount),
                        value = currencySymbol + LocaleFormatter.formatNumber(
                            amount.toString(),
                            2,
                            LocalContext.current
                        ),
                        style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(50.dp))
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = Color.White,
            style = if (status.length < 13)
                MaterialTheme.typography.bodyLarge
            else
                MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TransactionDetail(
    label: String,
    value: String,
    style: TextStyle = MaterialTheme
        .typography
        .bodyLarge
        .copy(
            color = MaterialTheme.colorScheme.primary
        )
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(color = DarkGrayAlternate)
        )
        Text(
            text = value,
            style = style
        )
    }
}

@Preview(locale = "es")
@Composable
private fun PreviewReceiptsScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader(
                    navigationIcon = {
                        AATTextButton(
                            text = "",
                            onClick = {},
                            modifier = Modifier.align(Alignment.Start),
                            textColor = MaterialTheme.colorScheme.secondary,
                        )
                    }
                )
            },
        ) { innerPadding ->
            val today =
                remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }
            val yesterday = remember(today) {
                today - 1.days
            }

            val transactions = listOf(
                ReceiptsListItem.Separator(dateTime = today),
                ReceiptsListItem.Item(
                    typeOfOperation = ReceiptTransactionTypeName.Sale,
                    localTransactionDateTime = LocalDateTime(today.date, LocalTime(16, 35, 40)),
                    authCode = "040946137",
                    driver = "Trevor Gantt",
                    quantity = 6500.23,
                    receiptId = 1,
                    unitOfMeasure = "L",
                    currencySymbol = "$"
                ),
                ReceiptsListItem.Separator(dateTime = yesterday),
                ReceiptsListItem.Item(
                    typeOfOperation = ReceiptTransactionTypeName.Completion,
                    localTransactionDateTime = LocalDateTime(
                        today.date.minus(1, DateTimeUnit.DAY),
                        LocalTime(16, 35, 40)
                    ),
                    authCode = "040946137",
                    vehicle = "AB123QA",
                    amount = 50.00,
                    receiptId = 1,
                    unitOfMeasure = "L",
                    currencySymbol = "$"
                ),
                ReceiptsListItem.Separator(
                    dateTime = LocalDateTime(
                        year = 2024,
                        monthNumber = 9,
                        dayOfMonth = 16,
                        hour = 16,
                        minute = 35,
                        second = 40
                    )
                ),
                ReceiptsListItem.Item(
                    typeOfOperation = ReceiptTransactionTypeName.Completion,
                    localTransactionDateTime = LocalDateTime(
                        year = 2024,
                        monthNumber = 9,
                        dayOfMonth = 16,
                        hour = 16,
                        minute = 35,
                        second = 40
                    ),
                    authCode = "040946137",
                    vehicle = "AB123QA",
                    amount = 50.00,
                    receiptId = 1,
                    unitOfMeasure = "L",
                    currencySymbol = "$"
                )
            )

            val items = MutableStateFlow(
                PagingData.from(
                    transactions,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(true),
                        append = LoadState.NotLoading(true),
                        prepend = LoadState.NotLoading(true)
                    ),
                )
            )

            ReceiptsScreen(
                transactions = items.collectAsLazyPagingItems(),
                modifier = Modifier
                    .padding(innerPadding),
                onReceiptClick = { id: Int -> /* TODO */ }
            )
        }
    }
}