package com.example.app.ui.theme

import androidx.compose.ui.tooling.preview.Preview

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Preview(
    name = "Android",
    device = "spec:width=720px,height=1280px,dpi=294",
    showSystemUi = true,
    showBackground = true
)
annotation class AndroidPreview