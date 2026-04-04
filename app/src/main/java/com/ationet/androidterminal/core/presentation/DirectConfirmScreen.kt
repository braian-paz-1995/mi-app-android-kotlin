package com.ationet.androidterminal.core.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme

@Composable
fun ProcessConfirmScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    @RawRes lottieRawRes: Int? = null,
    @DrawableRes iconRes: Int? = null,
    buttons: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFE7E7E7)),
                        startY = 0f
                    ),
                    center = Offset(size.width / 2, -300f),
                    radius = 1500f
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1) Título
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 35.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                // 2) Mensaje
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 4.28f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(26.dp))

                // 3) Ícono o Lottie (debajo del mensaje)
                when {
                    lottieRawRes != null -> {
                        val composition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(lottieRawRes)
                        )
                        LottieAnimation(
                            composition = composition,
                            modifier = Modifier
                                .size(180.dp)
                                .padding(bottom = 8.dp)
                        )
                    }
                    iconRes != null -> {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(96.dp)
                                .padding(top = 8.dp)
                        )
                    }
                    else -> {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            buttons()
        }
    }
}
/* ============================
 * PREVIEW
 * ============================ */

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 640,
    name = "ProcessConfirmScreen - Icon Preview"
)
@Composable
fun ProcessConfirmScreenPreview() {
    AtionetAndroidTerminalTheme {
        ProcessConfirmScreen(
            title = "Transacción a autorizar",
            message = "Reversa",
            iconRes = android.R.drawable.ic_menu_info_details,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            buttons = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Button(
                        onClick = { /* preview */ },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Confirmar") }

                    OutlinedButton(
                        onClick = { /* preview */ },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Cancelar") }
                }
            }
        )
    }
}