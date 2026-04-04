package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun PrintingCopyScreen(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize()
    ) {
        Box {
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(AATIcons.loading))

            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.printing_copy),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )

                Image(
                    painter = painterResource(id = AATIcons.printingCopy),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

                Text(
                    text = stringResource(R.string.printing_copy_please_wait),
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight(400)
                )
            }

            LottieAnimation(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(60.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
        }
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
            PrintingCopyScreen(
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}