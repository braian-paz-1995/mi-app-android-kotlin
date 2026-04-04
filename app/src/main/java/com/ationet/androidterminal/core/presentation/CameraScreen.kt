package com.ationet.androidterminal.core.presentation

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.await
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.presentation.util.debounced
import com.ationet.androidterminal.ui.theme.AATColorScheme
import com.ationet.androidterminal.ui.theme.AATIconScheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit,
    onError: () -> Unit,
    onQrCodeAnalysisComplete: (String) -> Unit,
) {
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    if (permissionState.status.isGranted) {
        CameraPreview(
            onBackClick = onBackClick,
            onQrCodeAnalysisComplete = {
                onQrCodeAnalysisComplete.invoke(it)
            }
        )
    } else {
        LaunchedEffect(Unit) {
            onError()
        }

        PermissionNotGranted()
    }
}

@Composable
private fun PermissionNotGranted() {
    Text(text = stringResource(R.string.permission_denied))
}

@Composable
private fun CameraPreview(
    onBackClick: () -> Unit,
    onQrCodeAnalysisComplete: (String) -> Unit,
) {
    val context = LocalContext.current

    val cameraController = remember {
        LifecycleCameraController(context)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraController.unbind()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) { padding ->
        var torchState by remember { mutableStateOf(false) }
        var torchOperationInProgress by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        CameraPreview(
            cameraController = cameraController,
            torchState = torchState,
            onBackNavigation = onBackClick,
            onTorchStateChanged = { newTorchState ->
                val hasFlashUnit = cameraController.cameraInfo?.hasFlashUnit() == true
                if(hasFlashUnit && !torchOperationInProgress) {
                    torchOperationInProgress = true
                    coroutineScope.launch {
                        runCatching {
                            cameraController.enableTorch(newTorchState).await()
                        }
                        torchState = newTorchState
                        torchOperationInProgress = false
                    }
                }
            },
            onQrCodeAnalysisComplete = onQrCodeAnalysisComplete,
            modifier = Modifier.padding(padding)
        )
    }

}

@Composable
private fun CameraPreview(
    cameraController: LifecycleCameraController,
    torchState: Boolean,
    modifier: Modifier = Modifier,
    onBackNavigation: () -> Unit,
    onTorchStateChanged: (Boolean) -> Unit,
    onQrCodeAnalysisComplete: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val barcodeScanner = remember {
        val options: BarcodeScannerOptions = BarcodeScannerOptions.Builder()
            .enableAllPotentialBarcodes()
            .build()

        BarcodeScanning.getClient(options)
    }

    DisposableEffect(Unit) {
        onDispose {
            barcodeScanner.close()
        }
    }

    BackHandler { onBackNavigation.invoke() }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                val view = PreviewView(it)
                val executor = ContextCompat.getMainExecutor(it)
                val analyzer = MlKitAnalyzer(
                    listOf(barcodeScanner),
                    COORDINATE_SYSTEM_VIEW_REFERENCED,
                    executor
                ) { result ->
                    val scan = result.getValue(barcodeScanner)
                    if (scan != null && scan.isNotEmpty()) {
                        val qr = scan.firstOrNull()
                        if (qr != null && !qr.rawValue.isNullOrBlank()) {
                            onQrCodeAnalysisComplete(qr.rawValue.orEmpty())
                        }
                    }
                }

                cameraController.setImageAnalysisAnalyzer(
                    ContextCompat.getMainExecutor(it),
                    analyzer
                )

                cameraController.bindToLifecycle(lifecycleOwner)
                view.controller = cameraController

                view
            },
            modifier = Modifier.fillMaxSize(),
        )

        Button(
            onClick = debounced(onBackNavigation),
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(
                    width = 80.dp,
                    height = 80.dp
                )
                .padding(20.dp),
            colors = ButtonDefaults.buttonColors(AATColorScheme.primary),
            contentPadding = PaddingValues(start = 2.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
        ) {
            val icon = painterResource(id = R.drawable.arrow_back)
            Icon(
                painter = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(25.dp)
            )
        }
        Button(
            onClick = debounced({
                onTorchStateChanged(!torchState)
            }),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(
                    width = 110.dp,
                    height = 110.dp
                )
                .padding(20.dp),
            colors = ButtonDefaults.buttonColors(AATColorScheme.primary),
            contentPadding = PaddingValues(4.dp)
        ) {
            val iconModifier = if (torchState) Modifier.size(30.dp) else Modifier
                .size(30.dp)
                .padding(start = 2.dp)
            Icon(
                painter = painterResource(
                    if (torchState)
                        AATIconScheme.flashOff
                    else
                        AATIconScheme.flashOn
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = iconModifier
            )
        }
    }
}