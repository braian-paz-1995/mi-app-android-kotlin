package com.example.app.menu

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.app.R

@Composable
fun VideoBackgroundView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Obtenemos la URI del video que guardamos en res/raw
    // Reemplaza "animacion_bloqueo" con el nombre real de tu archivo de video (sin la extensión .mp4)
    val videoUri = remember {
        Uri.parse("android.resource://${context.packageName}/${R.raw.animacion_bloqueo}")
    }

    // Usamos AndroidView para integrar la vista de video tradicional en Compose
    AndroidView(
        factory = { ctx ->
            VideoView(ctx).apply {
                // Configuramos la URI del video
                setVideoURI(videoUri)

                // Hacemos que el video se reproduzca en bucle (loop)
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    start() // Iniciamos la reproducción automáticamente
                }
            }
        },
        modifier = modifier.fillMaxSize() // Aseguramos que ocupe todo el espacio disponible
    )
}