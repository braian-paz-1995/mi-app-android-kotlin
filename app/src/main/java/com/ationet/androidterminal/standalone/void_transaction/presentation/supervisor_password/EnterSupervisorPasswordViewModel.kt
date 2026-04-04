package com.ationet.androidterminal.standalone.void_transaction.presentation.supervisor_password

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.use_case.configuration.GetConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface EnterSupervisorPasswordState {
    data class EnterSupervisorPassword(val supervisorPasswordError: SupervisorPasswordError? = null) : EnterSupervisorPasswordState
    data object EnterSupervisorPasswordOk : EnterSupervisorPasswordState
}

sealed class SupervisorPasswordError(@StringRes val message: Int) {
    data object Empty : SupervisorPasswordError(R.string.password_is_empty)
    data object Invalid : SupervisorPasswordError(R.string.password_is_invalid)
}

@HiltViewModel
class EnterSupervisorPasswordViewModel @Inject constructor(
    getConfiguration: GetConfiguration
) : ViewModel() {
    private val _state = MutableStateFlow<EnterSupervisorPasswordState>(EnterSupervisorPasswordState.EnterSupervisorPassword())
    val state = _state.asStateFlow()
    private val configuration = getConfiguration()

    fun onSupervisorPasswordEntered(supervisorPassword: String) {
        if (supervisorPassword.isEmpty()) {
            _state.value = EnterSupervisorPasswordState.EnterSupervisorPassword(SupervisorPasswordError.Empty)
            return
        }

        if (supervisorPassword != configuration.supervisorPassword) {
            _state.value = EnterSupervisorPasswordState.EnterSupervisorPassword(SupervisorPasswordError.Invalid)
            return
        }

        _state.value = EnterSupervisorPasswordState.EnterSupervisorPasswordOk
    }
}