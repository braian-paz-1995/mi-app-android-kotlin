package com.ationet.androidterminal.core.presentation

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.LoadingState
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import com.ationet.androidterminal.core.presentation.components.AATButton
import com.ationet.androidterminal.core.presentation.components.AATScaffold
import com.ationet.androidterminal.core.presentation.components.AATTextButton
import com.ationet.androidterminal.core.presentation.components.AATTopBar
import com.ationet.androidterminal.maintenance.settings.presentation.components.Subtitle
import com.ationet.androidterminal.ui.theme.NewlandPreview
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class PromptPhotoViewModel @Inject constructor(private val getConfiguration: GetConfiguration) :
    ViewModel() {

    private val _uiState = MutableStateFlow<PromptPhotoUiState>(PromptPhotoUiState.ShowPrompt)
    val uiState: StateFlow<PromptPhotoUiState> = _uiState
    private var lastBitmap: Bitmap? = null

    fun onConfirmPrompt() {
        _uiState.value = PromptPhotoUiState.ReadyToTakePhoto
    }

    fun onBitmapCaptured(bitmap: Bitmap) {
        lastBitmap = bitmap
        _uiState.value = PromptPhotoUiState.Processing(bitmap, LoadingState.Loading)
        processImage(bitmap)
    }

    private fun processImage(bitmap: Bitmap) {
        val configuration = getConfiguration()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val base64 = bitmap.toBase64()
                val request = ImageUploadRequest(
                    image = base64,
                    terminalIdentification = configuration.ationet.terminalId,
                    fileName = "${configuration.ationet.terminalId}_${System.currentTimeMillis()}.jpg"
                )
                val jsonRequest = Gson().toJson(request)

                sendImageRequest(
                    "plate",
                    jsonRequest,
                    viewModelScope,
                    configuration
                ) { success, responseBody ->
                    if (success && !responseBody.isNullOrBlank()) {
                        val plate = JSONObject(responseBody).optString("plate", "")
                        _uiState.value = PromptPhotoUiState.PhotoResult(bitmap, plate)
                    } else {
                        _uiState.value = PromptPhotoUiState.PhotoResult(bitmap, "")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PromptPhotoUiState.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
    }

    fun retry() {
        lastBitmap = null
        _uiState.value = PromptPhotoUiState.ReadyToTakePhoto
    }

    fun cancel() {
        _uiState.value = PromptPhotoUiState.ShowPrompt
    }

    private fun Bitmap.toBase64(): String {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}

sealed class PromptPhotoUiState {
    object ShowPrompt : PromptPhotoUiState()
    object ReadyToTakePhoto : PromptPhotoUiState()
    data class Processing(val bitmap: Bitmap, val loadingState: LoadingState) : PromptPhotoUiState()
    data class PhotoResult(val bitmap: Bitmap, val plate: String) : PromptPhotoUiState()
    data class Error(val message: String) : PromptPhotoUiState()
}

@Composable
internal fun PromptPhoto(
    @StringRes titleId: Int,
    onContinue: (String) -> Unit,
    onExit: () -> Unit,
) {
    val viewModel: PromptPhotoViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.onBitmapCaptured(bitmap)
        } else {
            onExit()
        }
    }

    // Check permission on composition
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        } else if (!permissionRequested) {
            permissionRequested = true
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    BackHandler(enabled = true) {
        when (uiState) {
            is PromptPhotoUiState.PhotoResult -> viewModel.retry()
            is PromptPhotoUiState.ReadyToTakePhoto -> onExit()
            else -> onExit()
        }
    }

    when (uiState) {
        is PromptPhotoUiState.ShowPrompt -> {
            MorePromptsScreenVision(
                modifier = Modifier.fillMaxSize(),
                onConfirmClicked = { viewModel.onConfirmPrompt() }
            )
        }

        PromptPhotoUiState.ReadyToTakePhoto -> {
            if (hasCameraPermission) {
                LaunchedEffect(Unit) { takePictureLauncher.launch(null) }
            }
        }

        is PromptPhotoUiState.Processing -> {
            val state = uiState as PromptPhotoUiState.Processing
            LoadingScreen(
                loadingState = state.loadingState,
                onSuccess = { /* opcional */ },
                onFailure = { /* opcional */ }
            )
        }

        is PromptPhotoUiState.PhotoResult -> {
            val state = uiState as PromptPhotoUiState.PhotoResult
            AATScaffold(
                topBar = {
                    AATTopBar(shouldDisplayNavigationIcon = true, shouldDisplayLogoIcon = true)
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = state.bitmap.asImageBitmap(),
                        contentDescription = stringResource(id = titleId),
                        modifier = Modifier.size(280.dp)
                    )

                    if (state.plate.isNotBlank()) {
                        Text(
                            text = "${stringResource(R.string.plate_detected)}: ${state.plate}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Text(stringResource(R.string.plate_not_detected), color = Color.Red)
                    }

                    AATButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onContinue(state.plate) },
                        enabled = state.plate.isNotBlank()
                    ) {
                        Text(stringResource(R.string.confirm))
                    }

                    AATTextButton(
                        text = stringResource(R.string.retry),
                        onClick = { viewModel.retry() },
                        textColor = MaterialTheme.colorScheme.primary
                    )

                    AATTextButton(
                        text = stringResource(R.string.cancel),
                        onClick = onExit,
                        textColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        is PromptPhotoUiState.Error -> {
            val message = (uiState as PromptPhotoUiState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = message, color = Color.Red)
                    AATButton(onClick = { viewModel.retry() }) {
                        Text(stringResource(R.string.retry))
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
}

/* Indica una una pantalla se esta procesado la imagen */
@NewlandPreview
@Composable
fun ImageProcessingScreen1(
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Subtitle(
                    stringResource(R.string.processing_image),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ImageProcessingScreen(
    loadingState: LoadingState
) {
    BackHandler { }

    LoadingScreen(
        loadingState = loadingState,
        onSuccess = { },
        onFailure = { }
    )
}

/* Nos indica una pantalla dando instrucciones al playero con lo que debe hacer con la camara */
@Composable
fun MorePromptsScreenVision(
    modifier: Modifier,
    onConfirmClicked: () -> Unit
) {
    BackHandler { }

    Surface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Subtitle(
                title = "\n" + stringResource(R.string.prompt_take_vehicle_picture) + "\n",
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(id = R.drawable.car_plate_instruction),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(vertical = 24.dp)
            )

            AATButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                onClick = onConfirmClicked
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    }
}

/* Envia el request a Vision para validar la patente */
fun sendImageRequest(
    promptKey: String,
    jsonRequest: String,
    viewModelScope: CoroutineScope,
    configuration: Configuration,
    onResult: (success: Boolean, responseBody: String?) -> Unit
) {
    viewModelScope.launch(Dispatchers.IO) {
        try {
            val url = URL(configuration.ationet.visionUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept-Language", "es-AR")
            conn.doOutput = true

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonRequest)
                writer.flush()
            }

            val responseCode = conn.responseCode
            val success = responseCode in 200..299

            val stream = if (success) conn.inputStream else conn.errorStream
            val responseBody = stream.bufferedReader().use { it.readText() }

            conn.disconnect()

            withContext(Dispatchers.Main) {
                if (success) {
                    println("setPromptValue(): key=$promptKey, value=$jsonRequest")
                    println("setPromptValue(): key=$promptKey, value=$responseBody")
                }
                onResult(success, responseBody)
            }
        } catch (e: Exception) {
            Log.e("Vision", "Error al procesar", e)
            withContext(Dispatchers.Main) {
                onResult(false, e.localizedMessage)
            }
        }
    }
}

data class ImageUploadRequest(
    val image: String,
    val fileName: String,
    val terminalIdentification: String
)
