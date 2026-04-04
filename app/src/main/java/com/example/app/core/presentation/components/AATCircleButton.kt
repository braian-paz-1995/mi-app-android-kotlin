package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleButton(
    iconRes: Int,
    title: String,
    size: Dp,
    isEnabled: Boolean,
    fontSize: Int = 15,
    lineHeight: Int = fontSize,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(CircleShape)
                .background(if (isEnabled) Color(0xFFEFEFEF) else Color(0xFFDEDEDE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = if (isEnabled) MaterialTheme.colorScheme.primary else Color(0xFF717171)
            )
        }
        Text(
            text = title,
            color = if (isEnabled) MaterialTheme.colorScheme.primary else Color(0xFF414141),
            textAlign = TextAlign.Center,
            fontSize = fontSize.sp,
            lineHeight = lineHeight.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}