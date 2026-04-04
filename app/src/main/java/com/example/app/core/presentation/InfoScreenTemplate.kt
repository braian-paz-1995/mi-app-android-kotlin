package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATInfoCard
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun InfoScreenTemplate(
    modifier: Modifier,
    imageModifier: Modifier = Modifier,
    title: String,
    imageRes: Int,
    imageSize: Dp,
    description: String,
    buttonText: String? = null,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit,
    exitButton: String? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AATHeaderTitle(title)
                Image(
                    painter = painterResource(id = imageRes),
                    modifier = imageModifier.width(imageSize),
                    contentDescription = null
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    ),
                )

                extraContent?.let {
                    it()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)
                ) {
                    if (buttonText != null) {
                        AATButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onConfirmClick,
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ) { Text(buttonText, fontSize = 16.sp) }
                    }

                    if (exitButton != null) {
                        AATTextButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = exitButton,
                            onClick = onCancelClick,
                            textColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}


@NewlandPreview
@Composable
private fun PendingTransactionFoundScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Pending transaction",
                imageRes = AATIcons.clock,
                imageSize = 230.dp,
                description = "There’s a pending transaction associated with this identification.\nPlease, complete the transaction or clear it to continue.",
                buttonText = "Clear transaction",
                exitButton = "Cancel",
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        onClick = {},
                        backgroundColor = MaterialTheme.colorScheme.secondary
                    ) { Text("Let's complete it!", fontSize = 16.sp) }
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun NoReceiptsFoundScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = stringResource(R.string.no_receipts_found),
                imageRes = AATIcons.empty,
                imageSize = 170.dp,
                description = stringResource(R.string.no_receipts_to_display),
                buttonText = stringResource(R.string.okay_exit),
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = stringResource(R.string.no_receipts_found_text)
                    )
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun NoFuelsFoundScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Oops! No fuels found",
                imageRes = AATIcons.empty,
                imageSize = 170.dp,
                description = "We couldn’t find any fuels associated with pump N° 3",
                buttonText = null,
                exitButton = "Cancel",
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = "Please, try again later.\nIf you are positive that there are products configured for the selected pump and you are still not seeing them, please contact ATIONET support."
                    )
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun BatchCloseErrorScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Batch close summary",
                imageRes = AATIcons.communicationError,
                imageSize = 250.dp,
                description = "Due to a communication error, we couldn’t complete the operation. Please, try again later.",
                buttonText = "Try again",
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = "Verify if the Wi-Fi is on and that you are connected to the internet.\nIf the problem persists, please contact ATIONET support. "
                    )
                }
            )
        }
    }
}


@NewlandPreview
@Composable
private fun BatchCloseScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Do you want to do a batch close?",
                imageRes = AATIcons.batch,
                imageSize = 230.dp,
                description = "",
                buttonText = "Yes",
                exitButton = "Cancel",
                onConfirmClick = {},
                onCancelClick = {},
            )
        }
    }
}


@NewlandPreview
@Composable
private fun CommunicationErrorAtionetScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Communication error with ATIONET",
                imageRes = AATIcons.communicationError,
                imageSize = 250.dp,
                description = "A communication error occurred while attempting to connect with Ationet.",
                buttonText = "Try again",
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = "If the problem persists, please contact ATIONET support. "
                    )
                }
            )
        }
    }
}


@NewlandPreview
@Composable
private fun CommunicationErrorFusionScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true
                )
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = stringResource(R.string.controller_communication_failure),
                imageRes = AATIcons.communicationError,
                imageSize = 280.dp,
                description = stringResource(R.string.controller_communication_error_description),
                buttonText = stringResource(R.string.try_again),
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = stringResource(R.string.contact_ationet_support)
                    )
                }
            )
        }
    }
}


@NewlandPreview
@Composable
private fun PrinterErrorScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Oops! There's an issue with the printer",
                imageRes = AATIcons.printerError,
                imageSize = 200.dp,
                description = "We detected a problem with the printer. You can try again or contact support for assistance.",
                buttonText = "Try again",
                extraContent = {
                    Text(
                        text = "Code error:" + "0320390243",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                    )
                },
                onConfirmClick = {},
                onCancelClick = {}
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PrinterOutOfPaperScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = "Oops! The printer is out of paper",
                imageRes = AATIcons.printerError,
                imageSize = 200.dp,
                description = "Please replace the paper roll and try printing againg.",
                buttonText = "All set! Try again",
                onConfirmClick = {},
                onCancelClick = {}
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PrinterOutOfPaper() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATHeader()
            },
        ) { innerPadding ->
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                imageModifier = Modifier.padding(start = 30.dp),
                title = "Error reading the identifier",
                imageRes = AATIcons.readingError,
                imageSize = 140.dp,
                description = "It seems there was an error reading the identifier, please try again.",
                buttonText = "Try again",
                onConfirmClick = {},
                onCancelClick = {},
                extraContent = {
                    AATInfoCard(
                        text = "Please make sure the identifier is correctly scanned or entered. If the error persists, try again or contact technical support for assistance."
                    )
                }
            )
        }
    }
}