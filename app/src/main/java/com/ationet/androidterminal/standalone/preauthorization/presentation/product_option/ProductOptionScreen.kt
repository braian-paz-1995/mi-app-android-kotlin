package com.ationet.androidterminal.standalone.preauthorization.presentation.product_option

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.components.AATButtonCard
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert

@Composable
fun ProductOptionScreen(
    viewModel: ProductOptionViewModel = hiltViewModel(),
    onQuantityAmountSelected: () -> Unit,
    onFillUpSelected: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit
) {

    when (viewModel.state.collectAsStateWithLifecycle().value) {
        ProductOptionState.ListProductOption -> {
            OptionList(
                onQuantityAmountSelected = onQuantityAmountSelected,
                onFillUpSelected = {
                    viewModel.resetQuantityState()
                    onFillUpSelected()
                },
                onBack = onBack,
                onExit = onExit
            )
        }
    }
}

@Composable
fun OptionList(
    onQuantityAmountSelected: () -> Unit,
    onFillUpSelected: () -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
    BackHandler { onBack() }

    var cancelSelection by remember {
        mutableStateOf(false)
    }

    if (cancelSelection) {
        Alert(
            onDismissDialog = { cancelSelection = false },
            onConfirmDialog = onExit
        )
    }

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = true,
                onBack = onBack,
                shouldDisplayLogoIcon = true
            )
        }
    ) { innerPadding ->
        OptionListContent(
            modifier = Modifier.padding(innerPadding),
            onQuantityAmountSelected = onQuantityAmountSelected,
            onFillUpSelected = onFillUpSelected,
            onCancel = { cancelSelection = true }
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun OptionListContent(
    modifier: Modifier,
    onQuantityAmountSelected: () -> Unit,
    onFillUpSelected: () -> Unit,
    onCancel: () -> Unit,
) {
    Surface(modifier = modifier) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize().padding(bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AATHeaderTitle(stringResource(R.string.fueling_option))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AATButtonCard(
                        height = 200.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = onQuantityAmountSelected
                    ) {
                        Text(
                            text = stringResource(R.string.quantity_amount),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    AATButtonCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        onClick = onFillUpSelected
                    ) {
                        Text(
                            text = stringResource(R.string.fill_up),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                AATTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onCancel,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}