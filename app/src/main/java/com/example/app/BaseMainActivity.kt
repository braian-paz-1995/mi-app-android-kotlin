package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.AATNavigationHost
import com.example.app.ui.theme.AtionetAndroidTerminalTheme
import com.example.app.ui.theme.LocalColorScheme
import com.example.app.ui.theme.LocalIconScheme

abstract class BaseMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val state = mainViewModel.state.collectAsStateWithLifecycle().value
            val colorScheme = state.colorScheme
            val iconScheme = state.iconScheme
            CompositionLocalProvider(
                LocalColorScheme provides colorScheme,
                LocalIconScheme provides iconScheme
            ) {
                AtionetAndroidTerminalTheme {
                    val navController = rememberNavController()
                    LaunchedEffect(Unit) {
                        navController.addOnDestinationChangedListener(listener)
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            navController.removeOnDestinationChangedListener(listener)
                        }
                    }
                    AATNavigationHost(navController = navController)
                }
            }
        }
    }

    /**
     * Holds logic for when the current navigation destionation changes.
     * */
    private val listener = object : NavController.OnDestinationChangedListener {
        override fun onDestinationChanged(
            controller: NavController,
            destination: NavDestination,
            arguments: Bundle?
        ) {
            val route = destination.route

            if (arguments != null) {
                Log.d(
                    TAG,
                    "Destination change with arguments. New route: '$route' - Arguments: '$arguments'"
                )
            } else {
                Log.d(TAG, "Destination change. New route: '$route'")
            }
        }
    }

    private companion object {
        private const val TAG: String = "MainActivity"
    }
}