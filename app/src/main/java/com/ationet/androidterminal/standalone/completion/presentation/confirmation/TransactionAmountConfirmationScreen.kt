package com.ationet.androidterminal.standalone.completion.presentation.confirmation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.presentation.SummaryScreen
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.core.util.LocaleFormatter
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun TransactionAmountConfirmationScreen(
    authorizationCode: String,
    product: String,
    authorizedQuantity: Double?,
    authorizedAmount: Double?,
    amount: Double,
    quantity: Double,
    currencySymbol: String,
    quantityUnit: String,
    language: Configuration.LanguageType,
    isCompanyPrice: Boolean,
    preAuthorizationType: DisplayType,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    onExit: () -> Unit,
) {
    BackHandler { onBack() }

    var cancelRead by remember {
        mutableStateOf(false)
    }

    if (cancelRead) {
        Alert(
            onDismissDialog = { cancelRead = false },
            onConfirmDialog = onExit
        )
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                shouldDisplayLogoIcon = true,
                onBack = onBack
            )
        }
    ) { paddingValues ->

        val preAuthCodeLabel = stringResource(R.string.pre_authorization_auth_code)
        val productNameLabel = stringResource(R.string.product)
        val authorizedQuantityLabel = stringResource(R.string.authorized_quantity)
        val authorizedAmountLabel = stringResource(R.string.authorized_amount)
        val amountSelected = stringResource(R.string.amount_selected)
        val quantitySelected = stringResource(R.string.quantity_selected)
        val quantityLabel = stringResource(R.string.quantity)
        val amountLabel = stringResource(R.string.amount)

        val summaryData = remember {
            buildMap {
                put(preAuthCodeLabel, authorizationCode)
                put(productNameLabel, product)

                if (authorizedQuantity != null && (preAuthorizationType == DisplayType.QUANTITY || preAuthorizationType == DisplayType.FILLUP)) {
                    put(
                        authorizedQuantityLabel,
                        ("${
                            authorizedQuantity.toString()?.let {
                                LocaleFormatter.formatNumber(
                                    it,
                                    3,
                                    language
                                )
                            }
                        }  $quantityUnit")
                    )
                    put(
                        quantitySelected,
                        ("${
                            quantity.toString()?.let {
                                LocaleFormatter.formatNumber(
                                    it,
                                    3,
                                    language
                                )
                            }
                        }  $quantityUnit")
                    )
                    put(amountLabel, ("$currencySymbol " + amount.toString()?.let {
                        LocaleFormatter.formatNumber(
                            it,
                            2,
                            language
                        )
                    }))
                }

                if (authorizedAmount != null && (preAuthorizationType == DisplayType.AMOUNT || preAuthorizationType == DisplayType.FILLUP)) {
                    put(
                        authorizedAmountLabel,
                        ("$currencySymbol ${
                            authorizedAmount.toString()?.let {
                                LocaleFormatter.formatNumber(
                                    it,
                                    2,
                                    language
                                )
                            }
                        }")
                    )
                    put(amountSelected, ("$currencySymbol " + amount.toString()?.let {
                        LocaleFormatter.formatNumber(
                            it,
                            2,
                            language
                        )
                    }))
                    if (!isCompanyPrice) {
                        put(quantityLabel, (quantity.toString()?.let {
                            LocaleFormatter.formatNumber(
                                it,
                                3,
                                language
                            )
                        } + " $quantityUnit"))
                    }
                }
            }
        }

        SummaryScreen(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 10.dp),
            title = stringResource(R.string.confirm_transaction),
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    AATButton(
                        onClick = onConfirm,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }

                    AATTextButton(
                        onClick = { cancelRead = true },
                        text = stringResource(id = R.string.cancel),
                        textColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                }
            },
            data = summaryData,
        )
    }
}

@NewlandPreview
@Composable
private fun Preview() {
    AtionetAndroidTerminalTheme {
        TransactionAmountConfirmationScreen(
            authorizationCode = "0303456",
            product = "Diesel",
            currencySymbol = "$",
            quantityUnit = "L",
            authorizedAmount = null,
            authorizedQuantity = 12.0,
            amount = 1.0,
            quantity = 2.0,
            language = Configuration.LanguageType.ES,
            preAuthorizationType = DisplayType.AMOUNT,
            onExit = { },
            onBack = { },
            onConfirm = { },
            isCompanyPrice = false
        )
    }
}