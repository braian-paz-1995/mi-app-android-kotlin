package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun IdentificationDoneScreen(
    image: Painter,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AATHeaderTitle(stringResource(R.string.identification_remove_title))

                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                )

                Text(
                    text = stringResource(R.string.identification_remove_text),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    ),
                )
            }
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewDoneMagnetic() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationDoneScreen(
                image = painterResource(AATIcons.cardSwipedRemove),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewDoneChip() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationDoneScreen(
                image = painterResource(AATIcons.cardInsertedRemove),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewDoneNfc() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationDoneScreen(
                image = painterResource(AATIcons.cardTappedRemove),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}
