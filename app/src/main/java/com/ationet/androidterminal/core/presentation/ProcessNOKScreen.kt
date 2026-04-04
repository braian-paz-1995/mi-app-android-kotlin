package com.ationet.androidterminal.core.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun ProcessNOKScreen(
    text: String,
    modifier: Modifier = Modifier,
    subText: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    BackHandler { }

    Column(
        modifier = modifier
    ) {
        /**
         * Header
         * */
        Box(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFE7E7E7)),
                        startY = 0f,
                    ),
                    center = Offset(size.width / 2, -320f),
                    radius = 1000f
                )
            }

            val composition by rememberLottieComposition(
                spec = LottieCompositionSpec.RawRes(AATIcons.processNOk)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(200.dp),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if(!subText.isNullOrBlank()) {
                    Text(
                        text = subText,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                        lineHeight = 35.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        /**
         * Content area
         * */
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content.invoke(this)
        }
    }
}

@Composable
fun ProcessNOKDescription(
    description: String,
    modifier: Modifier = Modifier
) {
    val localDescription = remember(description) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 18.sp)) {
                appendLine(description)
            }
        }
    }
    
    Text(
        text = localDescription,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@Composable
fun ProcessNOKTwoLineDescription(
    description: String,
    auxiliaryText: String,
    modifier: Modifier = Modifier
) {
    val localDescription = remember(description) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 16.sp)) {
                appendLine(auxiliaryText)
            }

            withStyle(style = SpanStyle(fontSize = 18.sp)) {
                appendLine(description)
            }
        }
    }

    Text(
        text = localDescription,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
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
            ProcessNOKScreen(
                text = "Something went wrong!",
                subText = "The operation was cancelled.",
                modifier = Modifier.padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                ),
                content = {
                    ProcessNOKDescription(
                        description = "Terminal ID invalid",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewTwoLines() {
    AtionetAndroidTerminalTheme {
        AATScaffold(
            topBar = {
                AATHeader(modifier = Modifier.background(Color.White))
            }
        ) { paddingValues ->
            ProcessNOKScreen(
                text = "Something went wrong!",
                subText = "The operation was cancelled.",
                modifier = Modifier.padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                ),
                content = {
                    ProcessNOKTwoLineDescription(
                        auxiliaryText = "(0303456)",
                        description = "Terminal ID invalid",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            )
        }
    }
}