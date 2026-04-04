package com.ationet.androidterminal.core.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ationet.androidterminal.R
import com.ationet.androidterminal.maintenance.settings.presentation.components.Subtitle
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.NewlandPreview

@Composable
fun MorePromptsScreen(
    modifier: Modifier
) {
    BackHandler {  }

    Surface(modifier = modifier) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Subtitle(
                    stringResource(R.string.looks_like_we_need_more_information_to_process_the_transaction),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@NewlandPreview
@Composable
private fun PreviewIdentificationScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = { }
        ) { innerPadding ->
            MorePromptsScreen(
                modifier = Modifier
                    .padding(innerPadding)
            )
        }
    }
}