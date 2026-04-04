package com.ationet.androidterminal.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.KeyboardArrowLeft
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.navigation.FleetGraph
import com.ationet.androidterminal.core.navigation.MaintenanceGraph
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun AATScaffold(
    modifier: Modifier = Modifier,
    shouldDisplayNavigationIcon: Boolean = false,
    onBack: () -> Unit = {},
    topBar: @Composable () -> Unit = {
        AATTopBar(
            shouldDisplayNavigationIcon = shouldDisplayNavigationIcon,
            onBack = onBack,
            shouldDisplayLogoIcon = true
        )
    },
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AATTopBar(
    shouldDisplayNavigationIcon: Boolean,
    shouldDisplayLogoIcon: Boolean,
    isHomeScreen: Boolean = false,
    onBack: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = if (isHomeScreen)
                    Alignment.CenterVertically
                else
                    Alignment.Bottom,
                modifier = Modifier.height(70.dp)
            ) {
                if (shouldDisplayLogoIcon) {
                    Image(
                        painter = painterResource(AATIcons.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(50.dp)
                    )
                }
                if (isHomeScreen) {
                    Image(
                        painter = painterResource(AATIcons.logo),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                    Image(
                        painter = painterResource(AATIcons.isotype),
                        contentDescription = null,
                        modifier = Modifier.width(100.dp)
                    )

                }
            }
        },
        navigationIcon = {
            if (shouldDisplayNavigationIcon) {
                Box(
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .height(70.dp),
                    contentAlignment = Alignment.TopStart // Alinea el contenido verticalmente arriba
                ) {
                    AATTextButton(
                        onClick = onBack,
                        text = stringResource(R.string.go_back),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        textColor = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    )
}

@Composable
fun AATHeader(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        navigationIcon?.invoke(this)
        Image(
            painter = painterResource(id = AATIcons.logo),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun ColumnScope.AATNavigationIcon(
    onClick: () -> Unit
) {
    AATTextButton(
        modifier = Modifier.align(Alignment.Start),
        onClick = onClick,
        text = stringResource(R.string.go_back),
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.TwoTone.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        textColor = MaterialTheme.colorScheme.secondary
    )
}

@NewlandPreview
@Composable
private fun PreviewAATHeader() {
    val buttonNavigation = listOf(FleetGraph.StandAlone, MaintenanceGraph)
    AATScaffold(
        shouldDisplayNavigationIcon = true,
        onBack = {},
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.clip(
                    RoundedCornerShape(
                        topStartPercent = 20,
                        topEndPercent = 20
                    )
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(buttonNavigation.size) {
                        AATTextButton(
                            onClick = {},
                            text = buttonNavigation[it]::class.simpleName.toString(),
                            textColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        },
        content = {}
    )
}