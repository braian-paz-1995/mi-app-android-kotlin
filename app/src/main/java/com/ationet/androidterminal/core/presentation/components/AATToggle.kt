package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.util.DisplayType
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview


@Composable
fun AATToggle(
    modifier: Modifier = Modifier,
    checked: DisplayType,
    onCheckedChange: (DisplayType) -> Unit
) {
    val roundedCorner = 60.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 10.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(roundedCorner)
            )
            .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(roundedCorner))
            .padding(7.dp)
    ) {
        Row {
            ToggleOption(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.quantity),
                isSelected = checked == DisplayType.QUANTITY,
                onClick = { onCheckedChange(DisplayType.QUANTITY) }
            )
            ToggleOption(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.amount),
                isSelected = checked == DisplayType.AMOUNT,
                onClick = { onCheckedChange(DisplayType.AMOUNT) }
            )
        }
    }
}

@Composable
private fun ToggleOption(
    modifier: Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val switchColorSelected = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val switchColorUnselected = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary
    val roundedCorner = 60.dp
    val shadow = if (isSelected) 10.dp else 0.dp

    Surface(
        modifier = modifier
            .shadow(shadow, shape = RoundedCornerShape(roundedCorner)),
        shape = RoundedCornerShape(roundedCorner),
        color = Color.Transparent,
        contentColor = LocalContentColor.current,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .height(60.dp)
                .background(switchColorSelected, RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = switchColorUnselected,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewAATToggle() {
    var isChecked by remember { mutableStateOf(DisplayType.AMOUNT) }

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
                AATToggle(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it }
                )
            }
        }
    }
}