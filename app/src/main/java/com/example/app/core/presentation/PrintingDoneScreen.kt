package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun PrintingDoneScreen(
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.done),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(id = AATIcons.printed),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = stringResource(R.string.grab_ticket),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight(400)
                )
            }

            Column(
                verticalArrangement = Arrangement
                    .spacedBy(10.dp),
                modifier = modifier
                    .padding(10.dp)
                    .align(Alignment.BottomCenter)
            ) {
                actions.invoke(this)
            }
        }
    }
}

@Composable
fun PrintingDonePrintCopyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AATButtonIcon(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.print_copy),
                        fontSize = 16.sp
                    )

                    Icon(
                        painter = painterResource(id = AATIcons.printerIcon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun PrintingDoneAcceptButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    AATButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        Text(
            text = stringResource(R.string.okay),
            fontSize = 16.sp
        )
    }
}

@NewlandPreview
@Composable
private fun Preview() {
    AtionetAndroidTerminalTheme {
        AATScaffold(
            topBar = {
                AATHeader(modifier = Modifier.background(Color.White))
            }
        ) { paddingValues ->
            PrintingDoneScreen(
                modifier = Modifier.padding(paddingValues),
                actions = {
                    PrintingDonePrintCopyButton(
                        onClick = { }
                    )
                    PrintingDoneAcceptButton(
                        onClick = { }
                    )
                }
            )
        }
    }
}