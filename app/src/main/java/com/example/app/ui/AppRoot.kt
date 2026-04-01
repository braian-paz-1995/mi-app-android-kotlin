package com.example.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mecanse.PantallaFotos
import com.example.mecanse.PantallaJuegoMenu
import com.example.mecanse.PantallaLista
import com.example.mecanse.WorkHourTracker
import com.example.mecanse.infoEventos
import com.example.mecanse.infoFarmeo
import com.example.mecanse.infoHeroes
import com.example.mecanse.infoInvestigaciones
import com.example.mecanse.lastz.PantallaContenidoWikiintroduccion
import com.example.mecanse.lastz.PantallaContenidoWikisede
import com.example.mecanse.lastz.infoIntroduccion
import com.example.mecanse.lastz.infoSede

private object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val NOTES = "notes"
    const val HORARIOS = "horarios"
    const val WIKI_MENU = "wiki_menu"
    const val INTRODUCCION = "introduccion"
    const val SEDE_RECURSOS = "sede_recursos"
    const val HEROES_EJERCITO = "heroes_ejercito"
    const val INVESTIGACIONES = "investigaciones"
    const val FARMEO = "farmeo"
    const val EVENTOS = "eventos"
    const val FOTOS = "fotos"
}

@Composable
fun AppRoot(
    state: AppUiState,
    onLogin: (String, String) -> Unit,
    onAddNote: (String) -> Unit,
    onUpdateNfcPayload: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onToggleFlashlight: () -> Unit,
    isFlashlightOn: Boolean,
    isFlashlightAvailable: Boolean
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeHubScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                token = state.loginToken,
                error = state.error,
                onLogin = { email, password ->
                    onLogin(email, password)
                    navController.navigate(Routes.NOTES)
                }
            )
        }
        composable(Routes.NOTES) {
            NotesScreen(
                state = state,
                onAddNote = onAddNote,
                onUpdateNfcPayload = onUpdateNfcPayload,
                onOpenCamera = onOpenCamera,
                onToggleFlashlight = onToggleFlashlight,
                isFlashlightOn = isFlashlightOn,
                isFlashlightAvailable = isFlashlightAvailable
            )
        }
        composable(Routes.HORARIOS) {
            WorkHourTracker(context = LocalContext.current, navController = navController)
        }
        composable(Routes.WIKI_MENU) {
            PantallaJuegoMenu(navController = navController)
        }
        composable(Routes.INTRODUCCION) {
            PantallaContenidoWikiintroduccion(
                titulo = "Introducción",
                contenido = infoIntroduccion,
                navController = navController
            )
        }
        composable(Routes.SEDE_RECURSOS) {
            PantallaContenidoWikisede(
                titulo = "Sede y recursos",
                contenido = infoSede,
                navController = navController
            )
        }
        composable(Routes.HEROES_EJERCITO) {
            PantallaLista(
                titulo = "Héroes y ejército",
                lista = infoHeroes,
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Routes.INVESTIGACIONES) {
            PantallaLista(
                titulo = "Investigaciones",
                lista = infoInvestigaciones,
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Routes.FARMEO) {
            PantallaLista(
                titulo = "Farmeo",
                lista = infoFarmeo,
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Routes.EVENTOS) {
            PantallaLista(
                titulo = "Eventos",
                lista = infoEventos,
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Routes.FOTOS) {
            PantallaFotos(navController = navController)
        }
    }
}

@Composable
private fun HomeHubScreen(navController: NavHostController) {
    val withViewModel = listOf(
        "Login" to Routes.LOGIN,
        "Notas + NFC + Cámara" to Routes.NOTES
    )

    val withoutViewModel = listOf(
        "Gestor de horas" to Routes.HORARIOS,
        "Wiki Last Z (menú)" to Routes.WIKI_MENU,
        "Introducción Last Z" to Routes.INTRODUCCION,
        "Sede y recursos" to Routes.SEDE_RECURSOS,
        "Héroes y ejército" to Routes.HEROES_EJERCITO,
        "Investigaciones" to Routes.INVESTIGACIONES,
        "Farmeo" to Routes.FARMEO,
        "Eventos" to Routes.EVENTOS,
        "Galería de fotos" to Routes.FOTOS
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Home", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Accesos organizados por pantallas con ViewModel y pantallas sin ViewModel.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Funciones con ViewModel", style = MaterialTheme.typography.titleMedium)
                    withViewModel.forEach { (label, route) ->
                        Button(
                            onClick = { navController.navigate(route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Funciones sin ViewModel", style = MaterialTheme.typography.titleMedium)
                    withoutViewModel.forEach { (label, route) ->
                        Button(
                            onClick = { navController.navigate(route) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    token: String?,
    error: String?,
    onLogin: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("demo@demo.com") }
    var password by rememberSaveable { mutableStateOf("123456") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Base de login", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Button(onClick = { onLogin(email, password) }) {
            Text("Ingresar")
        }
        token?.let { Text("Token: $it") }
        error?.let { Text("Error: $it") }
    }
}

@Composable
private fun NotesScreen(
    state: AppUiState,
    onAddNote: (String) -> Unit,
    onUpdateNfcPayload: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onToggleFlashlight: () -> Unit,
    isFlashlightOn: Boolean,
    isFlashlightAvailable: Boolean
) {
    var noteText by rememberSaveable { mutableStateOf("") }
    var nfcText by rememberSaveable(state.nfcPayload) { mutableStateOf(state.nfcPayload) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Notas locales (Room)", style = MaterialTheme.typography.titleLarge)
        Text("NFC disponible: ${state.nfcAvailable}")
        OutlinedTextField(
            value = nfcText,
            onValueChange = { nfcText = it },
            label = { Text("Texto para emisor NFC (HCE)") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onUpdateNfcPayload(nfcText) },
            enabled = state.nfcAvailable
        ) {
            Text("Guardar texto NFC")
        }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onOpenCamera,
                modifier = Modifier.weight(1f)
            ) {
                Text("Prender cámara")
            }
            Button(
                onClick = onToggleFlashlight,
                enabled = isFlashlightAvailable,
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isFlashlightOn) "Apagar linterna" else "Prender linterna")
            }
        }
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Nueva nota") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                onAddNote(noteText)
                noteText = ""
            }) {
                Text("Guardar")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.notes) { note ->
                Text("• ${note.content}")
            }
        }
    }
}
