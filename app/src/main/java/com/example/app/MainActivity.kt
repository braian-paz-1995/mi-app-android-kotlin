package com.example.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.app.menu.AppRoot
import com.example.app.menu.AppViewModel

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkBiometricAvailability(this)

        setContent {
            val context = LocalContext.current
            val viewModel: AppViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()

            var isAuthenticated by remember { mutableStateOf(false) }

            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

            val flashCameraId = remember {
                cameraManager.cameraIdList.firstOrNull { id ->
                    cameraManager.getCameraCharacteristics(id)
                        .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                }
            }

            var isFlashlightOn by remember { mutableStateOf(false) }

            var hasCameraPermission by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
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

            if (isAuthenticated) {
                AppRoot(
                    state = state,
                    onLogin = viewModel::login,
                    onAddNote = viewModel::addNote,
                    onUpdateNfcPayload = viewModel::updateNfcPayload,
                    onOpenCamera = {
                        if (!hasCameraPermission) {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
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

            } else {
                LockScreen(
                    onAuthenticated = { isAuthenticated = true }
                )
            }
        }
    }

    private fun checkBiometricAvailability(context: Context) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {}
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(context, context.getString(R.string.biometric_no_hardware), Toast.LENGTH_LONG).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(context, context.getString(R.string.biometric_hw_unavailable), Toast.LENGTH_LONG).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(context, context.getString(R.string.biometric_none_enrolled), Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun LockScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current

    fun initialAuthentication() {
        autenticarConHuella(context) { success ->
            if (success) onAuthenticated()
        }
    }

    LaunchedEffect(Unit) {
        initialAuthentication()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        VideoBackground()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.lock_screen_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { initialAuthentication() }) {
                Text(stringResource(R.string.retry_button))
            }
        }
    }
}

fun autenticarConHuella(context: Context, onResult: (Boolean) -> Unit) {
    val activity = context as FragmentActivity
    val executor = ContextCompat.getMainExecutor(context)

    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(true)
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(context, context.getString(R.string.auth_failed), Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(code: Int, msg: CharSequence) {
                Toast.makeText(context, context.getString(R.string.auth_error, msg), Toast.LENGTH_SHORT).show()
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(context.getString(R.string.auth_dialog_title))
        .setSubtitle(context.getString(R.string.auth_dialog_subtitle))
        .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        .build()

    biometricPrompt.authenticate(promptInfo)
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoBackground() {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.animacion_bloqueo}")
            setMediaItem(MediaItem.fromUri(uri))
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
