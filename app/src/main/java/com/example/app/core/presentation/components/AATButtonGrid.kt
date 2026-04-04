package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonGrid(
    items: List<HomePrincipalButton>,
    modifier: Modifier = Modifier,
    isButtonDisabled: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]

            GridButton(
                title = item.title,
                iconRes = item.icon,
                onClick = item.onClick,
                size = item.size,
                buttonHeight = 135.dp,
                isButtonDisabled = isButtonDisabled
            )
        }
    }
}

data class ButtonData(
    val iconRes: Int,
    val title: String,
    val size: Dp,
    val onClick: () -> Unit,
    val fontSize: Int = 15,
    val lineHeight: Int = fontSize
)

data class HomePrincipalButton(
    val title: String,
    val icon: Int,
    val size: Dp,
    val onClick: () -> Unit
)
