package com.ationet.androidterminal.core.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATBasicDialog
import com.ationet.androidterminal.core.presentation.components.AATTextButton

@Composable
fun ExitConfirmAlert(
    onDismissDialog: () -> Unit,
    onConfirmDialog: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AATBasicDialog(
            contentText = {
                Text(
                    text = stringResource(R.string.are_you_sure_you_want_to_exit),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.you_will_be_taken_to_the_home_screen),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            },
            contentButtons = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AATTextButton(
                        onClick = onDismissDialog,
                        text = stringResource(R.string.no),
                        textColor = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = onConfirmDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C5774)
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.yes),
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            onDismissRequest = onDismissDialog,
        )
    }
}