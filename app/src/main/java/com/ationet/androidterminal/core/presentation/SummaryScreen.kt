package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.DarkGrayAlternate
import com.ationet.androidterminal.ui.theme.Gray
import com.ationet.androidterminal.ui.theme.NewlandPreview

enum class SummaryStatus {
    SUCCESS, FAILURE
}
@Composable
fun SummaryScreen(
    modifier: Modifier,
    title: String,
    data: Map<String, String>,
    status: SummaryStatus? = null,
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    prominentStyle: TextStyle? = null,
    buttons: @Composable BoxScope.() -> Unit
) {
    val listState = rememberLazyListState()

    Surface {
        Box(modifier = modifier.fillMaxSize()) {
            Column {
                if (status != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        StatusBadge(status = status)
                    }
                } else if (icon != null) {
                    Column { icon.invoke(this) }
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Box {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.8f)
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
                                        style = if (useBold && prominentStyle != null) {
                                            prominentStyle
                                        } else {
                                            TextStyle(
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        textAlign = TextAlign.End
                                    )
                                }
                                HorizontalDivider(color = Gray.copy(alpha = 0.5f))
                            }
                        }
                        item { Box(modifier = Modifier.size(12.dp)) }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(vertical = 20.dp)
                    .background(color = Color.White)
            ) {
                buttons.invoke(this@Box)
            }
        }
    }
}
@Composable
private fun StatusBadge(
    status: SummaryStatus,
    modifier: Modifier = Modifier
) {
    val (bg, icon: ImageVector, cd) = when (status) {
        SummaryStatus.SUCCESS -> Triple(Color(0xFF1DB954), Icons.Rounded.CheckCircle, "Operation successful")
        SummaryStatus.FAILURE -> Triple(MaterialTheme.colorScheme.error, Icons.Rounded.Clear, "Operation failed")
    }

    Surface(
        modifier = modifier.size(100.dp).padding(10.dp),
        shape = CircleShape,
        color = bg
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = cd,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewSummaryScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Completion summary",
                data = mapOf(
                    "Date" to "09/30/2024 1:30 PM",
                    "Auth Code" to "09123456",
                    "Product" to "Premium Diesel",
                    "Amount completed" to "$ 50.00",
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButtonIcon(
                            onClick = {},
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
                                        Text("Print", fontSize = 16.sp)
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
                            onClick = {},
                            text = "Exit",
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewBalanceEnquiryScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Balance enquiry summary",
                icon = {
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp),
                        shape = CircleShape,
                        color = Color(0xFF3A8AB0)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(50.dp),
                                painter = painterResource(id = AATIcons.balanceInquiry),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                },
                data = mapOf(
                    "Client" to "112 - Trevor\n Grant",
                    "Product" to "4",
                    "Available" to "500.000 L",
                ),
                prominentStyle = TextStyle(
                    fontSize = 24.sp,
                    color = Color(0xFF007F68),
                    fontWeight = FontWeight.Bold
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButtonIcon(
                            onClick = {},
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
                                        Text("Print", fontSize = 16.sp)
                                        Icon(
                                            painter = painterResource(id = AATIcons.printerIcon),
                                            contentDescription = null,
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                }
                            }
                        )
                        AATButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {},
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ) { Text("Go back to home") }
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewBatchCloseScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Batch close summary",
                data = mapOf(
                    "Batch ID" to "0409461387",
                    "Sales count" to "4",
                    "Sales amount" to "$ 400.00",
                    "Cancelled sales count" to "1",
                    "Cancelled sales amount" to "$ 100.00",
                    "recharge quantity amount" to "$ 100.00",
                    "recharge total count" to "1",
                    "reverse quantity amount" to "$ 100.00",
                    "reverse total count" to "1",

                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButtonIcon(
                            onClick = {},
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
                                        Text("Print", fontSize = 16.sp)
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
                            text = "Exit",
                            onClick = {},
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewTransactionToVoidScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Transaction to void",
                data = mapOf(
                    "Date" to "08/08/2024 6:40 PM",
                    "Auth Code" to "035821104",
                    "Transaction Type" to "Sale",
                    "Amount" to "$67.89",
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButtonIcon(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            content = {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Yes, void it", fontSize = 16.sp)
                                }
                            }
                        )
                        AATTextButton(
                            text = "Exit",
                            onClick = {},
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewPinChangeScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "PIN Change summary",
                data = mapOf(
                    "Date" to "08/08/2024 6:40 PM",
                    "Auth Code" to "035821104",
                    "Error code" to "9001",
                    "Error message" to "Old PIN invalid",
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButtonIcon(
                            onClick = {},
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
                                        Text("Print", fontSize = 16.sp)
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
                            text = "Exit",
                            onClick = {},
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewSaleScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Sale summary",
                icon = {
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(50.dp),
                                painter = painterResource(id = AATIcons.balanceInquiry),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                },
                data = mapOf(
                    "Date" to "08/30/2024 01:30 PM",
                    "Auth Code" to "#04095617",
                    "Driver" to "Trevor Grant",
                    "Product" to "01 - Premium Diesel",
                    "Quantity" to "6,500.23 L",
                ),
                prominentStyle = TextStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {},
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ) { Text("Print copy") }
                    }
                }
            )
        }
    }
}


@NewlandPreview
@Composable
private fun PreviewConfirmationScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader(
                    navigationIcon = {
                        AATTextButton(
                            text = "Go back",
                            onClick = {},
                            modifier = Modifier.align(Alignment.Start),
                            textColor = MaterialTheme.colorScheme.secondary,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            SummaryScreen(
                modifier = Modifier.padding(
                    bottom = innerPadding.calculateBottomPadding(),
                    top = innerPadding.calculateTopPadding()
                ),
                title = "Confirmation summary",
                icon = {
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(10.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(50.dp),
                                painter = painterResource(id = AATIcons.balanceInquiry),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                },
                data = mapOf(
                    "Date" to "08/30/2024 01:30 PM",
                    "Auth Code" to "#04095617",
                    "Vehicle" to "AB123QA",
                    "Driver" to "Trevor Grant",
                    "Product" to "01 - Premium Diesel",
                    "Authorized amount" to "$ 6,500.23",
                    "Amount" to "$ 50.00",
                ),
                prominentStyle = TextStyle(
                    fontSize = 24.sp,
                    color = Color(0xFF007F68),
                    fontWeight = FontWeight.Bold
                ),
                buttons = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AATButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {},
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ) { Text("Print copy") }
                    }
                }
            )
        }
    }
}