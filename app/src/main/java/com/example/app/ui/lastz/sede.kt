package com.example.app.ui.lastz

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaContenidoWikisede(
    titulo: String,
    contenido: List<Pair<String, String>>,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
           // Fondo rojo solicitado
    ) {
        // 1. Imagen de fondo
        Image(
            // vvv CAMBIA ESTA LÍNEA vvv
            // Reemplaza 'ic_launcher_foreground' por el nombre de tu imagen en res/drawable
            painter = painterResource(id = R.drawable.mi_fondo_zombies),
            // ^^^ CAMBIA ESTA LÍNEA ^^^

            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop, // Crop ajusta la imagen a toda la pantalla
            alpha = 0.4f // Controla la transparencia de la imagen sobre el fondo rojo
        )

        // 2. Estructura de la pantalla
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(titulo, color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                items(contenido) { item ->
                    // Estado para controlar la expansión de cada tarjeta
                    var estaExpandido by remember { mutableStateOf(false) }
                    val (subtitulo, textoLargo) = item

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .animateContentSize() // Animación de crecimiento
                            .clickable { estaExpandido = !estaExpandido },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Black.copy(alpha = 0.75f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Fila del título y la flecha
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = subtitulo,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp,
                                    color = Color.Yellow
                                )
                                Icon(
                                    imageVector = if (estaExpandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }

                            // Texto que se muestra solo al expandir
                            if (estaExpandido) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = textoLargo,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para contraer",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            } else {
                                Text(
                                    text = "Toca para ver consejos...",
                                    fontSize = 13.sp,
                                    color = Color.LightGray.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- DATOS DE LA WIKI ---
val infoSede = listOf(
    "Prioridad HQ" to """
        La sede será tu cuartel general donde construirás, mejorarás y reclutaras tu ejercito. 

        Tu prioridad principal será subir tu sede lo más rápido posible para ello te centraras principalmente en subir de nivel únicamente los edificios que te pida la sede como requisito para mejorarla. Siempre te pedirá tener el laboratorio al día e irá alternando entre murallas, edificio de la alianza y los distintos centros de reclutamiento.

        En cuanto a los edificios de producción de materias primas como la granja o el aserradero son totalmente secundarios. No van a ser ni de cerca vuestra principal fuente de suministro de recursos. Como recomendación no me olvidaría de ellos pero los tendría siempre varios niveles por debajo del nivel de vuestra sede.

        Para mi el orden correcto de prioridades sería:
        1. Sede.
        2. Edificios relacionados con la mejora de la sede (laboratorio, alianza, murallas).
        3. Campo de entrenamiento (Experiencia de héroes).
        4. Edificios de las facciones (Formación 1).
        5. Plaza de reunión (Fuerza de combate).
        6. Hospitales (Evitar muertes).
        7. Formación (Subir resto de escuadrones).
        8. Centro de producción (Carga de recursos).
        9. Edificios de mejoras pasivas (Biblioteca/Villa).
        10. Restaurante (Velocidad de reclutamiento).
    """.trimIndent(),

    "Recursos" to """
        Gestion/Obtención de recursos
        Vamos a empezar por la gestión de recursos que creo que es el tema más básico de todo. Tenemos 4 formas de conseguir recursos:
        
        - Generación pasiva de edificios de la sede.
        - Minado activo con tus formaciones en el mapa.
        - Recompensas en forma de consumibles.
        - Farmear (Saqueo de sedes enemigas).

        Generación pasiva de minas:
        Cuanto más alto sea el nivel de los centros de producción, mayor será la generación. Hay un máximo de almacenamiento; si no lo recoges, se detiene. Depende del nivel del edificio y de las tecnologías.

        Minado activo:
        Es tu segunda fuente más importante. El nivel de tu formación determina cuánta carga puedes minar. Usa modificadores (eventos, territorio de alianza, investigaciones) para ir más rápido.

        Recompensas:
        Cualquier actividad da consumibles. Nota: los cofres de exp dan más conforme más alta es tu sede; guárdalos si no los necesitas ya.

        Gestión de la gasolina:
        - Pasiva: 1 cada 450 segundos.
        - Sede: 100 cada 12 horas.
        - Katrina: 100 cada 24 horas (en el reset).

        Tip: En el Almacén, pulsa 'Estadísticas' (abajo a la derecha) para ver la cantidad total sumando consumibles.
    """.trimIndent()
)