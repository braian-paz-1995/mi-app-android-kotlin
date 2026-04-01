package com.example.mecanse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController



// --- PANTALLA 2: MENÚ DE LA WIKI (ESTILO LISTA) ---
@Composable
fun PantallaJuegoMenu(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Last Z: Survival", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Shooter", fontSize = 28.sp)
            Spacer(modifier = Modifier.height(40.dp))
        }

        val menuItems = listOf(
            "Introducción" to Destinos.INTRODUCCION,
            "Sede Y Recursos" to Destinos.SEDE_RECURSOS,
            "Héroes Y Ejercito" to Destinos.HEROES_EJERCITO,
            "Investigaciones" to Destinos.INVESTIGACIONES,
            "Farmeo" to Destinos.FARMEO,
            "Eventos" to Destinos.EVENTOS
        )

        items(menuItems) { (titulo, ruta) ->
            TextButton(
                onClick = { navController.navigate(ruta) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(titulo, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Black)
                }
            }
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) {
                Text("Volver")
            }
        }
    }
}





val infoHeroes = listOf(
    "Composición" to "Usa héroes del mismo tipo (Tanque, Misil, Avión) para obtener bonos de daño.",
    "Héroes UR" to "Enfócate en subir las estrellas de tus héroes dorados para el primer escuadrón."
)

val infoInvestigaciones = listOf(
    "Duelo de Alianzas" to "Mejora esto para obtener las 9 cajas de recompensas diarias en el VS.",
    "Fuerzas Especiales" to "Es la investigación más larga, pero necesaria para desbloquear tropas T10."
)

val infoFarmeo = listOf(
    "Recolección" to "Envía tus escuadrones secundarios a minas de nivel alto antes de desconectarte.",
    "Radar" to "Limpia el radar cada 12 horas para maximizar componentes de drones."
)

val infoEventos = listOf(
    "Calendario VS" to "Lunes: Héroes, Martes: Construcción, Miércoles: Tecnología, Jueves: Tren, Viernes: Unidades.",
    "Invasión" to "Ataca camiones zombies con tu alianza para obtener medallas de honor."
)
@Composable
fun PantallaGenerica(titulo: String, contenido: String, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text(contenido, fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onVolver) { Text("Volver") }
    }
}

@Composable
fun PantallaLista(titulo: String, lista: List<Pair<String, String>>, onVolver: () -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        item {
            Text(titulo, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(lista) { (subtitulo, desc) ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(subtitulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(desc)
                }
            }
        }
        item {
            Button(onClick = onVolver, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text("Volver")
            }
        }
    }
}