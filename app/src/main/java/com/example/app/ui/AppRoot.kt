package com.example.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
    NavHost(navController = navController, startDestination = "notes") {
        composable("login") {
            LoginScreen(
                token = state.loginToken,
                error = state.error,
                onLogin = { email, password ->
                    onLogin(email, password)
                    navController.navigate("notes")
                }
            )
        }
        composable("notes") {
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
