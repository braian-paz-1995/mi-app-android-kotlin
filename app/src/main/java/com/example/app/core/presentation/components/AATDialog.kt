package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ationet.androidterminal.ui.theme.NewlandPreview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AATBasicDialog(
    modifier: Modifier = Modifier,
    contentText: @Composable ColumnScope.() -> Unit,
    contentButtons: @Composable ColumnScope.() -> Unit,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(),
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = properties,
        content = {
            Surface(
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(30.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    contentText()
                    Spacer(modifier = Modifier.height(12.dp))
                    contentButtons()
                }
            }

        },
    )
}

@NewlandPreview
@Composable
private fun PreviewAATDialog() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AATBasicDialog(
            contentText = {
                Text(
                    text = "Are you sure you want to cancel?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "You will be taken to the home screen.",
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
                        onClick = {},
                        text = "No",
                        textColor = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C5774)
                        ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Yes",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            onDismissRequest = {},
        )
    }
}