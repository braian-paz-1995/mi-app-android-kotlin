package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun IdentificationReadingScreen(
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
                AATHeaderTitle(stringResource(R.string.id_verification_in_progress))

                IdentificationReadingAnimation(
                    image = image
                )

                IdentificationReadingInfoCard()
            }

            IdentificationReadingInProgressIndicator()
        }
    }
}

@Composable
private fun IdentificationReadingAnimation(
    image: Painter,
) {
    Image(
        painter = image,
        contentDescription = stringResource(R.string.identification_inserted),
        modifier = Modifier
            .size(200.dp)
    )
}

@Composable
private fun IdentificationReadingInfoCard() {
    Text(
        text = stringResource(R.string.fetching_identification_data_please_wait_a_few_seconds),
        style = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        ),
    )
}

@Composable
private fun BoxScope.IdentificationReadingInProgressIndicator() {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(AATIcons.loading)
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(60.dp),
    )
}

@NewlandPreview
@Composable
private fun PreviewReadingMagnetic() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationReadingScreen(
                image = painterResource(AATIcons.cardSwiped),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewReadingChip() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationReadingScreen(
                image = painterResource(AATIcons.cardInserted),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewReadingNfc() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = {
                AATTopBar(
                    shouldDisplayNavigationIcon = false,
                    shouldDisplayLogoIcon = true,
                )
            }
        ) { innerPadding ->
            IdentificationReadingScreen(
                image = painterResource(AATIcons.cardTapped),
                modifier = Modifier
                    .padding(innerPadding),
            )
        }
    }
}
