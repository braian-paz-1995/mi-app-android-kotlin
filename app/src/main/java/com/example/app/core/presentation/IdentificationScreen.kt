package com.ationet.androidterminal.core.presentation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATInfoCard
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.util.IdentificationType
import com.ationet.androidterminal.fusion.home.presentation.IdentificationAnimation
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun IdentificationScreen(
    modifier: Modifier = Modifier,
    onScanClicked: () -> Unit,
    onManualEntryClicked: () -> Unit,
    onBack: () -> Unit = {},
    showCancelButton: Boolean = false
) {
    Column(
        modifier = modifier
            .padding(10.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IdentificationAnimation(show = true)

        IdentificationInfoCard()

        ActionButtons(
            modifier = Modifier,
            onScanClicked = onScanClicked,
            onManualEnterClicked = onManualEntryClicked,
        )

        if (showCancelButton) {
            AATTextButton(
                onClick = onBack,
                text = stringResource(R.string.cancel),
                textColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun IdentificationInfoCard() {
    AATInfoCard(
        text = stringResource(R.string.identification_swipe_insert_tap_text),
    )
    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
private fun ActionButtons(
    modifier: Modifier = Modifier,
    onScanClicked: () -> Unit,
    onManualEnterClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AATButtonIcon(
            onClick = onScanClicked,
            modifier = Modifier.fillMaxWidth(),
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.identification_scan_button_text))
                    Spacer(modifier = Modifier.width(25.dp))
                    Icon(
                        painterResource(id = AATIcons.camera),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        )
        AATButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onManualEnterClicked,
            backgroundColor = MaterialTheme.colorScheme.primary
        ) {
            Text(stringResource(R.string.identification_manual_entry_text))
        }
    }
}

/**
 * Screen shown while card readers are active and user can select to open camera or enter
 * the identification manually.
 * */
@Composable
fun IdentificationReaderScreen(
    @StringRes titleId: Int = R.string.confirmation_identification_title,
    onBack: () -> Unit,
    onScanClicked: () -> Unit,
    onManualEntryClicked: () -> Unit,
    showBackButton: Boolean = true,
    showCancelButton: Boolean = false
) {
    BackHandler {
        if (showBackButton) {
            onBack.invoke()
        }
    }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = showBackButton,
                shouldDisplayLogoIcon = true,
                onBack = onBack
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            AATHeaderTitle(stringResource(titleId))

            IdentificationScreen(
                modifier = Modifier.padding(it),
                onScanClicked = onScanClicked,
                onManualEntryClicked = onManualEntryClicked,
                onBack = onBack,
                showCancelButton = showCancelButton
            )
        }
    }
}

/**
 * Screen shown while card is being read
 * */
@Composable
fun IdentificationReadInProgressScreen(
    type: IdentificationType,
) {
    BackHandler { }

    val image = identificationReadImageByType(type = type)

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            IdentificationReadingScreen(
                image = image
            )
        }
    }
}

@Composable
private fun identificationReadImageByType(
    type: IdentificationType
): Painter {
    return when (type) {
        IdentificationType.Magnetic -> painterResource(id = AATIcons.cardSwiped)
        IdentificationType.Chip -> painterResource(id = AATIcons.cardInserted)
        IdentificationType.Nfc -> painterResource(id = AATIcons.cardTapped)
    }
}

/**
 * Screen shown while card has been read, and is waiting to be removed from reader
 * */
@Composable
fun IdentificationWaitingToRemoveScreen(
    type: IdentificationType
) {
    BackHandler { }

    val image = identificationDoneImageByType(type = type)

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            IdentificationDoneScreen(
                image = image
            )
        }
    }
}

@Composable
private fun identificationDoneImageByType(
    type: IdentificationType
): Painter {
    return when (type) {
        IdentificationType.Magnetic -> painterResource(id = AATIcons.cardSwipedRemove)
        IdentificationType.Chip -> painterResource(id = AATIcons.cardInsertedRemove)
        IdentificationType.Nfc -> painterResource(id = AATIcons.cardTappedRemove)
    }
}

@Composable
fun IdentificationErrorScreen(
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    BackHandler { }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) {
        InfoScreenTemplate(
            modifier = Modifier.padding(it),
            imageModifier = Modifier.padding(start = 30.dp),
            title = stringResource(R.string.identification_error_title),
            imageRes = AATIcons.readingError,
            imageSize = 140.dp,
            description = stringResource(R.string.identification_error_description),
            buttonText = stringResource(R.string.identification_retry_button),
            onConfirmClick = onRetry,
            onCancelClick = onExit,
            exitButton = stringResource(R.string.exit),
            extraContent = {
                AATInfoCard(
                    text = stringResource(R.string.identification_error_card)
                )
            }
        )
    }
}

/**
 * Screen shown when card was not presented in the allowed time window
 * */
@Composable
fun TimeoutScreen(
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    BackHandler { }

    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) {
        InfoScreenTemplate(
            modifier = Modifier.padding(it),
            imageModifier = Modifier.padding(start = 30.dp),
            title = stringResource(R.string.identification_error_title),
            imageRes = AATIcons.readingError,
            imageSize = 140.dp,
            description = stringResource(R.string.identification_error_description),
            buttonText = stringResource(R.string.identification_retry_button),
            onConfirmClick = onRetry,
            onCancelClick = onExit,
            exitButton = stringResource(R.string.exit),
            extraContent = {
                AATInfoCard(
                    text = stringResource(R.string.identification_error_card)
                )
            }
        )
    }
}

/**
 * Screen shown when card the qr presented is wrong
 * */
@Composable
fun QRErrorScreen(
    onRetry: () -> Unit,
    onExit: () -> Unit,
) {
    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = true,
            )
        }
    ) {
        InfoScreenTemplate(
            modifier = Modifier.padding(it),
            imageModifier = Modifier.padding(start = 30.dp),
            title = stringResource(R.string.missing_information_in_qr_code),
            imageRes = AATIcons.readingError,
            imageSize = 140.dp,
            description = stringResource(R.string.qr_error_text),
            buttonText = stringResource(R.string.identification_retry_button),
            onConfirmClick = onRetry,
            onCancelClick = onExit,
            exitButton = stringResource(R.string.exit),
            extraContent = {
                AATInfoCard(
                    text = stringResource(R.string.qr_error_info_card)
                )
            }
        )
    }
}


@Composable
fun ManualEntryScreen(
    onBack: () -> Unit,
    onAcceptEntry: (String) -> Unit,
) {
    BackHandler {
        onBack.invoke()
    }
    AATScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                shouldDisplayLogoIcon = true,
                onBack = onBack
            )
        }
    ) { innerPadding ->
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(""))
        }

        AlphanumericPromptScreen(
            title = stringResource(R.string.identification_manual_enter_title),
            modifier = Modifier.padding(innerPadding),
            textFieldValue = textFieldValue,
            onTextFieldValueChanged = {
                textFieldValue = it.copy(text = it.text.trim())
            },
            onContinue = {
                onAcceptEntry.invoke(textFieldValue.text)
            }
        )
    }
}

@Composable
fun IdentificationScanScreen(
    onBack: () -> Unit,
    onIdentificationScanComplete: (String) -> Unit,
) {
    BackHandler {
        onBack.invoke()
    }

    CameraScreen(
        onBackClick = onBack,
        onError = { },
        onQrCodeAnalysisComplete = onIdentificationScanComplete
    )
}


@NewlandPreview
@Composable
private fun PreviewIdentificationScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                Column {
                    AATTopBar(
                        shouldDisplayNavigationIcon = true,
                        shouldDisplayLogoIcon = true,
                        onBack = { }
                    )

                    AATHeaderTitle(stringResource(R.string.identification_title))
                }
            }
        ) { innerPadding ->
            IdentificationScreen(
                modifier = Modifier
                    .padding(innerPadding),
                onScanClicked = { },
                onManualEntryClicked = { }
            )
        }
    }
}