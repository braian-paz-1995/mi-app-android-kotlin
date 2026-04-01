package com.example.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.app.ui.AppRoot
import com.example.app.ui.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val flashCameraId = remember(cameraManager) {
                cameraManager.cameraIdList.firstOrNull { id ->
                    cameraManager.getCameraCharacteristics(id)
                        .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
            }
            var isFlashlightOn by remember { mutableStateOf(false) }
            var hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                hasCameraPermission = granted
            }

            LaunchedEffect(Unit) {
                if (!hasCameraPermission) {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            AppRoot(
                state = state,
                onLogin = viewModel::login,
                onAddNote = viewModel::addNote,
                onUpdateNfcPayload = viewModel::updateNfcPayload,
                onOpenCamera = {
                    if (!hasCameraPermission) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else {
                        startActivity(Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE))
                    }
                },
                onToggleFlashlight = {
                    if (!hasCameraPermission) {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && flashCameraId != null) {
                        val nextState = !isFlashlightOn
                        cameraManager.setTorchMode(flashCameraId, nextState)
                        isFlashlightOn = nextState
                    }
                },
                isFlashlightOn = isFlashlightOn,
                isFlashlightAvailable = flashCameraId != null
            )
        }
    }
}
