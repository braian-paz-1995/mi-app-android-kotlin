package com.example.mecanse.lastz

// --- TODOS ESTOS IMPORTS SON NECESARIOS ---
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContenidoWikiintroduccion(
    titulo: String,
    contenido: List<Pair<String, String>>,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues -> // Se especifica el nombre para evitar error de inferencia
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(contenido) { item -> // Usamos 'item' para evitar errores de desestructuración
                val (sub, txt) = item

                // Este estado recuerda si esta tarjeta está abierta o cerrada
                var expandido by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .animateContentSize() // Animación suave al crecer
                        .clickable { expandido = !expandido }, // Al tocar, expande
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = sub,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        if (expandido) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = txt,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Text(
                                text = "Toca para cerrar",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        } else {
                            Text(
                                text = "Toca para saber más...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Lista actualizada con "Last Z"
val infoIntroduccion = listOf(
    "¿Qué es Last Z?" to "Es un juego de estrategia y supervivencia donde debes gestionar tu base y derrotar zombies.",
    "Primeros Pasos" to "Sigue las misiones del capítulo y no gastes aceleradores innecesarios al inicio."
)