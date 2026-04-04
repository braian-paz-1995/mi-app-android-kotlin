package com.ationet.androidterminal.core.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun LoadingScreen(
    loadingState: LoadingState,
    text: String = stringResource(R.string.please_wait),
    onSuccess: () -> Unit, onFailure: () -> Unit,
    fontSize: TextUnit = 28.sp
) {
    BackHandler { }

    // Load compositions
    val compositionLoader by rememberLottieComposition(LottieCompositionSpec.RawRes(AATIcons.ationetLoader))
    val compositionSuccess by rememberLottieComposition(LottieCompositionSpec.RawRes(AATIcons.ationetSuccess))
    val compositionFailure by rememberLottieComposition(LottieCompositionSpec.RawRes(AATIcons.ationetFailure))

    // Determine which composition to show based on loading state
    val currentComposition = when (loadingState) {
        LoadingState.Loading -> compositionLoader
        LoadingState.Success -> compositionSuccess
        LoadingState.Failure -> compositionFailure
    }
    val iterations = if (loadingState == LoadingState.Loading) LottieConstants.IterateForever else 1

    if (loadingState == LoadingState.Success) {
        LaunchedEffect(true) {
            delay(2.seconds)
            onSuccess()
        }
    } else if (loadingState == LoadingState.Failure) {
        LaunchedEffect(true) {
            delay(2.seconds)
            onFailure()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        ConstraintLayout(
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            val (constText, image) = createRefs()

            Text(
                text = text,
                fontSize = fontSize,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.constrainAs(constText) {
                    bottom.linkTo(image.top, margin = 60.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            LottieAnimationComposable(
                composition = currentComposition,
                iterations = iterations,
                modifier = Modifier.constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}

@Composable
fun LottieAnimationComposable(
    composition: LottieComposition?,
    iterations: Int,
    modifier: Modifier = Modifier
) {
    // Using key to force recomposition based on composition
    key(composition) {
        LottieAnimation(
            composition = composition,
            iterations = iterations,
            modifier = modifier.size(200.dp)
        )
    }
}

@NewlandPreview
@Composable
private fun PreviewLoadingScreen() {
    AtionetAndroidTerminalTheme {
        val loadingState = remember { mutableStateOf<LoadingState>(LoadingState.Loading) }

        LaunchedEffect(true) {
            delay(5.seconds)
            loadingState.value = LoadingState.Success
        }

        LoadingScreen(loadingState = loadingState.value, onSuccess = {}, onFailure = {})
    }
}

@NewlandPreview
@Composable
private fun PreviewSendingLogScreen() {
    AtionetAndroidTerminalTheme {
        val loadingState = remember { mutableStateOf<LoadingState>(LoadingState.Loading) }

        LaunchedEffect(true) {
            delay(5.seconds)
            loadingState.value = LoadingState.Success
        }

        LoadingScreen(
            loadingState = loadingState.value,
            text = stringResource(R.string.sending_logs),
            onSuccess = {},
            onFailure = {}
        )
    }
}

@NewlandPreview
@Composable
private fun PreviewSendingKeepAliveScreen() {
    AtionetAndroidTerminalTheme {
        val loadingState = remember { mutableStateOf<LoadingState>(LoadingState.Loading) }

        LaunchedEffect(true) {
            delay(5.seconds)
            loadingState.value = LoadingState.Success
        }

        LoadingScreen(
            loadingState = loadingState.value,
            text = stringResource(R.string.sending_keepalive),
            onSuccess = {},
            onFailure = {}
        )
    }
}