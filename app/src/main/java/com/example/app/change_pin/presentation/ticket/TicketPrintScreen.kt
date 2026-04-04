package com.ationet.androidterminal.core.change_pin.presentation.ticket

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATReceipt
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.maintenance.settings.presentation.DottedDivider
import com.ationet.androidterminal.ui.theme.AATIcons


@Composable
fun TicketPrintScreen(
    viewModel: TicketViewModel = hiltViewModel(),
    onPrint: () -> Unit,
    onExit: () -> Unit
) {
    val uiModel = viewModel.uiModel.collectAsState().value

    if (uiModel == null) {
        return
    }
    BackHandler { }

    var isPrinting by remember { mutableStateOf(false) }

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                TicketPrintingAnimation(
                    uiModel = uiModel,
                    isPrinting = isPrinting,
                    onFinish = {
                        onPrint()
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                AATButtonIcon(
                    onClick = {
                        if (!isPrinting) {
                            isPrinting = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(com.ationet.androidterminal.R.string.print), fontSize = 16.sp)
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
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.exit),
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun TicketPrintingAnimation(
    uiModel: TicketUiModel,
    isPrinting: Boolean,
    onFinish: () -> Unit
) {

    val offsetY = remember { Animatable(0f) }
    val scrollState = rememberScrollState()

    var ticketHeight by remember { mutableStateOf(0f) }

    LaunchedEffect(isPrinting, ticketHeight) {

        if (isPrinting && ticketHeight > 0f) {

            scrollState.scrollTo(0)

            offsetY.snapTo(0f)

            offsetY.animateTo(
                targetValue = -ticketHeight,
                animationSpec = tween(
                    durationMillis = 5000,
                    easing = LinearEasing
                )
            )

            onFinish()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF)),
        contentAlignment = Alignment.TopCenter
    ) {

        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clipToBounds()
        ) {

            if (isPrinting) {
                AATReceipt(
                    modifier = Modifier
                        .onGloballyPositioned {
                            ticketHeight = it.size.height.toFloat()
                        }
                        .graphicsLayer {
                            translationY = offsetY.value
                        }
                        .padding(horizontal = 16.dp)
                ) {

                    val ticketFontColor = Color.Black

                    Column {
                        DottedDivider(ticketFontColor)
                        TicketContent(uiModel)
                        DottedDivider(ticketFontColor)
                    }
                }
            } else {
                AATReceipt(
                    modifier = Modifier
                        .onGloballyPositioned {
                            ticketHeight = it.size.height.toFloat()
                        }
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp)
                ) {

                    val ticketFontColor = Color.Black

                    Column {
                        DottedDivider(ticketFontColor)
                        TicketContent(uiModel)
                        DottedDivider(ticketFontColor)
                    }
                }
            }
        }
    }
}
@Composable
fun TicketContent(uiModel: TicketUiModel) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {

        // HEADER (centrado como ticket real)
        uiModel.header.forEach {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FECHA / TRANSACCION / HORA
        Row(Modifier.fillMaxWidth()) {
            Text(uiModel.transactionHeader.first, Modifier.weight(1f))
            Text(
                uiModel.transactionHeader.second,
                Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                uiModel.transactionHeader.third,
                Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SITE INFO (izquierda)
        uiModel.siteInfo.forEach {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // TRANSACTION INFO (izquierda 👈 como ticket)
        uiModel.transactionInfo.forEach {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RESULTADO (centrado y grande)
        uiModel.resultMessage.forEach {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // FOOTER (centrado)
        uiModel.footer.forEach {
            Text(
                text = it,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}
@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 600
)
@Composable
fun TicketPreview() {

    val uiModel = fakeTicketUiModel()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        TicketPrintingAnimation(
            uiModel = uiModel,
            isPrinting = false,
            onFinish = {}
        )
    }
}
fun fakeTicketUiModel(): TicketUiModel {
    return TicketUiModel(
        header = listOf(
            "ATIONET S.A.",
            "CAMBIO DE PIN"
        ),
        transactionHeader = Triple(
            "03/27/2026",
            "(Change PIN)",
            "04:16:47 PM"
        ),
        siteInfo = listOf(
            "0001 - SUCURSAL CENTRO",
            "Av. Siempre Viva 742",
            "CUIT: 30-12345678-9"
        ),
        transactionInfo = listOf(
            "Terminal ID: 511123456",
            "Authorization code:",
            "024918150"
        ),
        ationetData = emptyList(),
        resultMessage = listOf(
            "Identificador",
            "inexistente"
        ),
        footer = listOf(
            "Gracias por operar con nosotros",
            "www.ationet.com"
        ),
        isSuccess = false
    )
}