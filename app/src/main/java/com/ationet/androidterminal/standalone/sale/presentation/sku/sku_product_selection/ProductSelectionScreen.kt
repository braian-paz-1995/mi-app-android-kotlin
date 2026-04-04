package com.ationet.androidterminal.standalone.sale.presentation.sku.sku_product_selection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.InfoScreenTemplate
import com.ationet.androidterminal.core.presentation.components.AATButtonCard
import com.ationet.androidterminal.core.presentation.components.AATHeaderTitle
import com.ationet.androidterminal.core.presentation.components.AATInfoCard
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.Alert
import com.ationet.androidterminal.standalone.sale.domain.model.ProductStandAlone


import com.ationet.androidterminal.ui.theme.AATIcons
import kotlinx.coroutines.delay

@Composable
fun SKUProductSelectionScreen(
    viewModel: ProductSelectionViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProductSelected: () -> Unit,
    onExit: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000L)
        isLoading = false
    }

    if (isLoading) {
        BackHandler { }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        when (val state = viewModel.state.collectAsStateWithLifecycle().value) {

            is ProductSelectionState.ListProductSelection -> {
                BackHandler {
                    onBack()
                }
                ListProducts(
                    products = state.products,
                    onProductSelected = {
                        viewModel.setProduct(it)
                        onProductSelected()
                    },
                    onBack = onBack,
                    onExit = onExit
                )
            }

        }
    }
}

@Composable
fun ListProducts(
    products: List<ProductStandAlone>,
    onProductSelected: (ProductStandAlone) -> Unit,
    onBack: () -> Unit,
    onExit: () -> Unit
) {
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
        if (products.isEmpty()) {
            InfoScreenTemplate(
                modifier = Modifier.padding(innerPadding),
                title = stringResource(R.string.oops_no_fuels_found),
                imageRes = AATIcons.empty,
                imageSize = 170.dp,
                description = stringResource(R.string.we_couldn_t_find_any_fuels),
                buttonText = null,
                exitButton = stringResource(R.string.cancel),
                onConfirmClick = {},
                onCancelClick = { cancelSelection = true },
                extraContent = {
                    AATInfoCard(
                        text = stringResource(R.string.no_fuels_info_card_standalone)
                    )
                }
            )
        } else {
            BackHandler { onBack() }

            ProductPickerScreen(
                modifier = Modifier.padding(innerPadding),
                products = products,
                onProductSelected = onProductSelected,
                onExit = onExit
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProductPickerScreen(
    modifier: Modifier = Modifier,
    products: List<ProductStandAlone>,
    onProductSelected: (ProductStandAlone) -> Unit,
    onExit: () -> Unit
) {
    Surface(modifier = modifier) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                AATHeaderTitle(stringResource(R.string.select_sku))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 25.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    products.forEach { product ->
                        AATButtonCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(125.dp),
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            onClick = {
                                onProductSelected(product)
                            }
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append(product.name)
                                },
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                AATTextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onExit,
                    textColor = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}