package com.ationet.androidterminal.standalone.home.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ationet.androidterminal.MainViewModel
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.navigation.FleetGraph
import com.ationet.androidterminal.core.navigation.LoyaltyGraph
import com.ationet.androidterminal.core.navigation.MaintenanceGraph
import com.ationet.androidterminal.core.navigation.TaskGraph
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.core.presentation.components.ButtonData
import com.ationet.androidterminal.core.presentation.components.ButtonGrid
import com.ationet.androidterminal.core.presentation.components.HomePrincipalButton
import com.ationet.androidterminal.core.presentation.components.ScrollableRow
import com.ationet.androidterminal.ui.theme.AATIcons
import com.ationet.androidterminal.ui.theme.AtionetAndroidTerminalTheme
import com.ationet.androidterminal.ui.theme.LightBlueAlternate
import com.ationet.androidterminal.ui.theme.NewlandPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StandAloneHomeScreen(
    onNavigation: (HomeDestination) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
    buttonBottomNavigation: List<Any>
) {
    val isButtonDisabled = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val isTaskEnabled = true
    val isLoyaltyEnabled = state.isLoyaltyEnabled
    rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(AATIcons.identificationReader)
    )

    val navigation: (HomeDestination) -> Unit = { destination ->
        if (!isButtonDisabled.value) {
            isButtonDisabled.value = true
            onNavigation(destination)
            scope.launch {
                delay(1000)
                isButtonDisabled.value = false
            }
        }
    }

    val items = listOf(
        HomePrincipalButton(
            title = stringResource(R.string.pre_authorization).uppercase(),
            icon = AATIcons.preAuthorization,
            size = 60.dp,
            onClick = { navigation(HomeDestination.PreAuthorization) }
        ),
        HomePrincipalButton(
            title = stringResource(R.string.completion).uppercase(),
            icon = AATIcons.completion,
            size = 60.dp,
            onClick = { navigation(HomeDestination.Completion) }
        ),
        HomePrincipalButton(
            title = stringResource(R.string.sale).uppercase(),
            icon = AATIcons.postPaid,
            size = 65.dp,
            onClick = { navigation(HomeDestination.Sale) }
        ),
        HomePrincipalButton(
            title = stringResource(R.string.receipts).uppercase(),
            icon = AATIcons.receipts,
            size = 50.dp,
            onClick = { navigation(HomeDestination.Receipts) }
        )
    )

    val buttons = listOf(
        ButtonData(
            iconRes = AATIcons.balanceEnquiry,
            title = stringResource(R.string.balance_enquiry),
            size = 50.dp,
            onClick = { navigation(HomeDestination.BalanceEnquiry) }
        ),
        ButtonData(
            iconRes = AATIcons.clearPending,
            title = stringResource(R.string.clear_pending_transactions),
            size = 52.dp,
            onClick = { navigation(HomeDestination.ClearPendingTransactions) }
        ),
        ButtonData(
            iconRes = AATIcons.voidTransaction,
            title = stringResource(R.string.void_transaction),
            size = 45.dp,
            onClick = { navigation(HomeDestination.VoidTransaction) }
        ),
//        ButtonData(
//            iconRes = AATIcons.changePin,
//            title = stringResource(R.string.change_pin),
//            size = 52.dp,
//            onClick = { navigation(HomeDestination.ChangePIN) }
//        ),
//        ButtonData(
//            iconRes = AATIcons.batchClose,
//            title = stringResource(R.string.batch_close),
//            size = 52.dp,
//            onClick = { navigation(HomeDestination.BatchClose) }
//        )
    )

    AATScaffold(
        topBar = {
            AATTopBar(
                shouldDisplayNavigationIcon = false,
                shouldDisplayLogoIcon = false,
                isHomeScreen = true
            )
        },
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
                    buttonBottomNavigation
                        .filter { button ->
                            when (button) {
                                TaskGraph -> isTaskEnabled
                                LoyaltyGraph -> isLoyaltyEnabled
                                else -> true
                            }
                        }                        .forEach { button ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        Log.d("NavigationDebug", "Clicked button: $button")
                                        when (button) {
                                            FleetGraph.StandAlone -> {
                                                Log.d("NavigationDebug", "Navigating to Home")
                                                onNavigation(HomeDestination.Home)
                                            }

                                            TaskGraph -> {
                                                Log.d("NavigationDebug", "Navigating to Task")
                                                onNavigation(HomeDestination.Task)
                                            }
                                            LoyaltyGraph -> {
                                                Log.d("NavigationDebug", "Navigating to Task")
                                                onNavigation(HomeDestination.Loyalty)
                                            }

                                            MaintenanceGraph -> {
                                                Log.d(
                                                    "NavigationDebug",
                                                    "Navigating to Maintenance"
                                                )
                                                onNavigation(HomeDestination.Maintenance)
                                            }

                                            else -> Log.d("NavigationDebug", "Unknown button")
                                        }
                                    }
                            ) {
                                val icon = when (button) {
                                    FleetGraph.StandAlone -> AATIcons.fleet
                                    TaskGraph -> AATIcons.task
                                    LoyaltyGraph -> AATIcons.loyalty
                                    MaintenanceGraph -> AATIcons.maintenance
                                    else -> AATIcons.fleet
                                }

                                val label = when (button) {
                                    FleetGraph.StandAlone -> stringResource(R.string.operations)
                                    TaskGraph -> stringResource(R.string.task)
                                    LoyaltyGraph -> stringResource(R.string.loyalty)
                                    MaintenanceGraph -> stringResource(R.string.maintenance)
                                    else -> ""
                                }

                                val isSelected = (button == FleetGraph.StandAlone)
                                val CIRCLE_SIZE = 100.dp
                                val INNER_PADDING = 5.dp
                                val ICON_SIZE = 28.dp
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Surface(
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(CIRCLE_SIZE)
                                            .clip(CircleShape)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(INNER_PADDING),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Image(
                                                    painter = painterResource(icon),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(ICON_SIZE),
                                                    colorFilter = if (isSelected)
                                                        ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                                                    else
                                                        ColorFilter.tint(LightBlueAlternate)
                                                )
                                                Spacer(Modifier.height(1.dp))
                                                Text(
                                                    text = label,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                                    color = if (isSelected)
                                                        MaterialTheme.colorScheme.onPrimary
                                                    else
                                                        LightBlueAlternate
                                                )
                                            }

                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    ) { innerPadding ->
        Content(
            modifier = Modifier.padding(innerPadding),
            items = items,
            buttons = buttons,
            isButtonDisabled = isButtonDisabled.value
        )
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    items: List<HomePrincipalButton>,
    buttons: List<ButtonData>,
    isButtonDisabled: Boolean
) {
    Surface {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                ButtonGrid(items = items, isButtonDisabled = isButtonDisabled)
                ScrollableRow(
                    buttons = buttons,
                    isButtonDisabled = isButtonDisabled,
                    isPendingEnabled = isButtonDisabled
                )
            }
        }
    }
}

@Composable
fun IdentificationAnimation(show: Boolean = false) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(AATIcons.identificationReader)
    )

    if (show) {
        Spacer(modifier = Modifier.height(5.dp))
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth()
                .scale(1.2f)
                .padding(start = 70.dp),
            composition = composition,
            iterations = LottieConstants.IterateForever,
            alignment = Alignment.Center,
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun BottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F9FF))
            .clip(RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            BottomBarItem(
                icon = R.drawable.preauthorization,
                label = stringResource(R.string.operations),
                selected = true
            )
            BottomBarItem(
                icon = R.drawable.task,
                label = stringResource(R.string.task),
                selected = false
            )
            BottomBarItem(
                icon = R.drawable.preauthorization,
                label = stringResource(R.string.maintenance),
                selected = false
            )
        }
    }
}

@Composable
fun BottomBarItem(
    icon: Int,
    label: String,
    selected: Boolean
) {
    val iconTint =
        if (selected) MaterialTheme.colorScheme.primary else LightBlueAlternate
    val textColor =
        if (selected) MaterialTheme.colorScheme.primary else LightBlueAlternate

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(38.dp)
        )
        Text(
            text = label,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AATHeaderHome() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LogoImage(imageResId = AATIcons.logo)
    }
}

@Composable
fun LogoImage(
    imageResId: Int,
    modifier: Modifier = Modifier.size(50.dp)
) {
    Image(
        painter = painterResource(id = imageResId),
        contentDescription = null,
        modifier = modifier
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@NewlandPreview
@Composable
private fun PreviewStandAloneHomeScreen() {
    AtionetAndroidTerminalTheme {
        Scaffold(
            topBar = { },
            bottomBar = { }
        ) { innerPadding ->
            StandAloneHomeScreen(
                onNavigation = {},
                buttonBottomNavigation = listOf(
                    FleetGraph.StandAlone,
                    TaskGraph,
                    LoyaltyGraph,
                    MaintenanceGraph
                )
            )
        }
    }
}
