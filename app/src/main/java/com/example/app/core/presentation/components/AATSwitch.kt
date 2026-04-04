package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.DarkGray
import com.ationet.androidterminal.ui.theme.Gray
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun AATSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val switchColor = if (!enabled) DarkGray else if (isChecked) MaterialTheme.colorScheme.primary else Gray
    
    Box(
        modifier = modifier
            .size(width = 50.dp, height = 30.dp)
            .background(switchColor, RoundedCornerShape(15.dp))
            .padding(horizontal = 1.dp),
        contentAlignment = if (isChecked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            modifier = Modifier
                .shadow(10.dp, shape = RoundedCornerShape(50.dp)),
            shape = RoundedCornerShape(50.dp),
            color = Color.Transparent,
            contentColor = LocalContentColor.current
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.White, CircleShape)
                    .clickable { if (enabled) onCheckedChange(!isChecked) }
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewAATSwitch() {
    AtionetAndroidTerminalTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AATSwitch(
                    isChecked = true,
                    onCheckedChange = { }
                )
                AATSwitch(
                    isChecked = false,
                    onCheckedChange = { }
                )
                AATSwitch(
                    isChecked = true,
                    onCheckedChange = { },
                    enabled = false
                )
            }
        }
    }
}