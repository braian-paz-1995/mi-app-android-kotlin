package com.example.app.menu

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.core.data.AppDatabase
import com.example.app.core.data.local.room.entity.menu.menu.NoteEntity
import com.example.app.core.data.repository.LoginRepositoryImpl
import com.example.app.menu.nfc.NfcPayloadStore
import com.example.app.menu.nfc.NfcStatusProvider
import com.example.app.core.data.repository.NoteRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppUiState(
    val email: String = "",
    val loginToken: String? = null,
    val notes: List<NoteEntity> = emptyList(),
    val nfcAvailable: Boolean = false,
    val nfcPayload: String = "",
    val error: String? = null
)

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val noteRepository = NoteRepositoryImpl(AppDatabase.getInstance(application).noteDao())
    private val loginRepositoryImpl = LoginRepositoryImpl()
    private val nfcStatusProvider = NfcStatusProvider(application)

    private val _uiState = MutableStateFlow(
        AppUiState(
            nfcAvailable = nfcStatusProvider.isNfcAvailable(),
            nfcPayload = NfcPayloadStore.getPayload(application)
        )
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.notes().collect { notes ->
                _uiState.update { it.copy(notes = notes) }
            }
        }
    }

    fun addNote(content: String) {
        viewModelScope.launch {
            noteRepository.addNote(content)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginRepositoryImpl.login(email, password)
                .onSuccess { token ->
                    _uiState.update { it.copy(email = email, loginToken = token, error = null) }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = throwable.message ?: "Error de login") }
                }
        }
    }

    fun updateNfcPayload(payload: String) {
        NfcPayloadStore.setPayload(getApplication(), payload)
        _uiState.update {
            it.copy(nfcPayload = NfcPayloadStore.getPayload(getApplication()))
        }
    }
}
