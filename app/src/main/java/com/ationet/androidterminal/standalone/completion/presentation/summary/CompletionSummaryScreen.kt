package com.ationet.androidterminal.standalone.completion.presentation.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.LoadingScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKScreen
import com.ationet.androidterminal.core.presentation.ProcessNOKTwoLineDescription
import com.ationet.androidterminal.core.presentation.ProcessOkAuthorizationCode
import com.ationet.androidterminal.core.presentation.ProcessOkScreen
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.ui.theme.AATIcons
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@Composable
fun CompletionSummaryScreen(
    viewModel: CompletionSummaryViewModel = hiltViewModel(),
    onExit: () -> Unit,
    onPrint: () -> Unit,
) {
    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is CompletionSummaryState.InProgress -> {
            CompletionInProgressScreen(
                loadingState = state.loadingState
            )
        }

        is CompletionSummaryState.Success -> {
            CompletionSucceededScreen(
                authorizationCode = state.authorizationCode
            )
        }

        is CompletionSummaryState.Summary -> {
            CompletionSummaryInfoScreen(
                date = state.date,
                authorizationCode = state.authorizationCode,
                product = state.product,
                completedQuantity = state.quantity,
                completedAmount = state.amount,
                currencySymbol = state.currencySymbol,
                quantityUnit = state.quantityUnit,
                language = state.language,
                completionType = state.completionType,
                onPrint = {
                    onPrint.invoke()
                },
                onExit = onExit
            )
        }

        is CompletionSummaryState.Error -> {
            LaunchedEffect(Unit) {
                delay(3.seconds)
                onPrint.invoke()
            }

            CompletionFailedScreen(
                errorCode = state.errorCode,
                message = state.message,
            )
        }

        CompletionSummaryState.CommunicationError -> {
            LaunchedEffect(Unit) {
                delay(3.seconds)
                onPrint.invoke()
            }

            val message = stringResource(R.string.ationet_communication_error)
            CompletionFailedScreen(
                errorCode = null,
                message = message,
            )
        }
    }
}

@Composable
private fun CompletionInProgressScreen(
    loadingState: LoadingState
) {
    BackHandler { }

    LoadingScreen(
        loadingState = loadingState,
        onSuccess = { },
        onFailure = { }
    )
}

@Composable
private fun CompletionSucceededScreen(
    authorizationCode: String,
) {
    BackHandler { }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        ProcessOkScreen(
            text = stringResource(R.string.your_transaction_has_been_completed),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {
                ProcessOkAuthorizationCode(
                    authorizationCode = authorizationCode,
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                )
            }
        )
    }
}

@Composable
private fun CompletionFailedScreen(
    errorCode: String?,
    message: String,
) {
    BackHandler { }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        ProcessNOKScreen(
            text = stringResource(R.string.something_went_wrong),
            subText = stringResource(R.string.the_operation_was_cancelled),
            modifier = Modifier.padding(
                bottom = paddingValues.calculateBottomPadding(),
                top = paddingValues.calculateTopPadding()
            ),
            content = {
                ProcessNOKTwoLineDescription(
                    description = message,
                    auxiliaryText = errorCode.orEmpty(),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        )
    }
}

@Composable
private fun CompletionSummaryInfoScreen(
    date: LocalDateTime,
    authorizationCode: String,
    product: String,
    completedQuantity: Double,
    completedAmount: Double,
    currencySymbol: String,
    quantityUnit: String,
    language: Configuration.LanguageType,
    completionType: DisplayType,
    onPrint: () -> Unit,
    onExit: () -> Unit,
) {
    var cancelRead by remember {
        mutableStateOf(false)
    }

    if (cancelRead) {
        Alert(
            onDismissDialog = { cancelRead = false },
            onConfirmDialog = onExit
        )
    }

    BackHandler { }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true
            )
        }
    ) { paddingValues ->
        val context = LocalContext.current
        val formattedDate = remember(date) {
            LocaleFormatter.formatDateTime(dateTime = date, context = context)
        }
        val dynamicDataOrder = mutableListOf<Pair<String, String>>()

        dynamicDataOrder.add(stringResource(R.string.summary_date) to formattedDate)
        dynamicDataOrder.add(stringResource(R.string.auth_code) to authorizationCode)
        dynamicDataOrder.add(stringResource(R.string.product) to product)

        if (completionType == DisplayType.AMOUNT) {
            dynamicDataOrder.add(
                stringResource(R.string.amount_completed) to ("$currencySymbol " + completedAmount.toString()
                    ?.let {
                        LocaleFormatter.formatNumber(it, 2, language)
                    })
            )
            dynamicDataOrder.add(
                stringResource(R.string.quantity_completed) to (completedQuantity.toString()?.let {
                    LocaleFormatter.formatNumber(it, 3, language)
                } + " $quantityUnit"))
        } else {
            dynamicDataOrder.add(
                stringResource(R.string.quantity_completed) to (completedQuantity.toString()?.let {
                    LocaleFormatter.formatNumber(it, 3, language)
                } + " $quantityUnit"))
            dynamicDataOrder.add(
                stringResource(R.string.amount_completed) to ("$currencySymbol " + completedAmount.toString()
                    ?.let {
                        LocaleFormatter.formatNumber(it, 2, language)
                    })
            )
        }

        SummaryScreen(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 10.dp),
            title = stringResource(R.string.completion_summary),
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
                        text = stringResource(id = R.string.exit),
                        onClick = { cancelRead = true },
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            },
            data = dynamicDataOrder.toMap(),
        )
    }
}