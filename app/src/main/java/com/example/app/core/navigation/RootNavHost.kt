package com.ationet.androidterminal.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ationet.androidterminal.MainViewModel
import com.ationet.androidterminal.fusion.navigation.navigationFusion
import com.ationet.androidterminal.loyalty.home.navegation.navigationLoyalty
import com.ationet.androidterminal.maintenance.navigation.navigationMaintenance
import com.ationet.androidterminal.standalone.navigation.navigationStandAlone
import com.ationet.androidterminal.task.home.navegation.navigationTask1

@Composable
fun RootNavHost(modifier: Modifier = Modifier, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        NavHost(modifier = modifier, navController = navController, startDestination = MaintenanceGraph) {
            navigationStandAlone(navController)
            navigationFusion(navController)
            navigationMaintenance(navController)
            navigationTask1(navController)
            navigationLoyalty(navController)
        }
    }
}

@Composable
fun AATNavigationHost(modifier: Modifier = Modifier, navController: NavHostController) {
    val viewModel = hiltViewModel<MainViewModel>()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    NavHost(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        navController = navController,
        startDestination = state.graph
    ) {
        navigationStandAlone(navController)
        navigationFusion(navController)
        navigationMaintenance(navController)
        navigationTask1(navController)
        navigationLoyalty(navController)
    }
}