package com.ationet.androidterminal.standalone.void_transaction.presentation.summary

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButtonIcon
import com.ationet.androidterminal.core.presentation.components.AATHeader
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.ui.theme.AATIcons

@Composable
fun SummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onPrint: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler {  }

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = {
                cancelSelection = false
                onExit()
            }
        )
    }

    when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
        is SummaryState.Summary -> {
            SummaryContent(
                summaryData = state.data,
                onPrint = onPrint,
                onCancel = { cancelSelection = true },
            )
        }
    }
}

@Composable
fun SummaryContent(
    summaryData: SummaryData,
    onPrint: () -> Unit,
    onCancel: () -> Unit,
) {

    val data = mapOf(
        stringResource(R.string.label_date) to summaryData.date,
        stringResource(R.string.authorization_code) to summaryData.authorizationCode,
    )

    AATScaffold(
        topBar = {
            AATHeader(modifier = Modifier.background(Color.White))
        }
    ) { innerPadding ->
        com.ationet.androidterminal.core.presentation.SummaryScreen(
            modifier = Modifier.padding(innerPadding),
            title = stringResource(R.string.void_summary),
            data = data,
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AATButtonIcon(
                        onClick = onPrint,
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(stringResource(R.string.print), fontSize = 16.sp)
                                    Icon(
                                        painter = painterResource(id = AATIcons.printerIcon),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    )
                    AATTextButton(
                        onClick = onCancel,
                        text = stringResource(R.string.exit),
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}