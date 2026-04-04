package com.example.app.ui.theme

import androidx.compose.runtime.Composable
import com.example.app.R
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.app.ui.util.IconScheme


val AATIconScheme = IconScheme(
    logo = R.drawable.android_logo,

)

val LocalIconScheme = staticCompositionLocalOf { AATIconScheme }

object AATIcons {
    val logo @Composable get() = LocalIconScheme.current.logo
}