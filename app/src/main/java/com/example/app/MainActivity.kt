package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.app.ui.AppRoot
import com.example.app.ui.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()
            AppRoot(
                state = state,
                onLogin = viewModel::login,
                onAddNote = viewModel::addNote,
                onUpdateNfcPayload = viewModel::updateNfcPayload
            )
        }
    }
}
