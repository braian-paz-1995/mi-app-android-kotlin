package com.ationet.androidterminal.core.presentation.util

import androidx.navigation.NavController

fun <T : Any> NavController.navigateAndPopUp(route: T) {
    navigate(route) {
        this@navigateAndPopUp.currentDestination?.let {
            popUpTo(it.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}