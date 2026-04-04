package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ationet.androidterminal.ui.theme.Gray
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun AATReceipt(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Column {
        Box(modifier = modifier.fillMaxWidth()) {
            content()
        }
        Canvas(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(20.dp)
        ) {
            val zigzagPath = Path().apply {
                val width = size.width
                val height = size.height
                moveTo(0f, 0f)
                val triangleWidth = width / 20

                for (i in 0 until 20) {
                    lineTo(triangleWidth * i + triangleWidth / 2, height)
                    lineTo(triangleWidth * (i + 1), 0f)
                }
                lineTo(width, (-5f))
            }
            drawPath(zigzagPath, Gray)
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewAATReceipt() {
    Column {
        AATReceipt(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.onSurface)
                .padding(25.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Text("Title", fontWeight = FontWeight.Bold, fontSize = 38.sp)
                Text("Subtitle", fontWeight = FontWeight.Bold, fontSize = 26.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("MM/DD/YYYY", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("(PREVIEW)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("hh:mm:ss", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Site code - Site name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text("Site address", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
                Spacer(modifier = Modifier.height(156.dp))
                Text("Footer", fontWeight = FontWeight.Bold, fontSize = 38.sp)
                Text("Bottom note", fontWeight = FontWeight.Bold, fontSize = 26.sp)
            }
        }
    }
}