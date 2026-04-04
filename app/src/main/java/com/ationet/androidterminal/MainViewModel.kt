package com.ationet.androidterminal

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ationet.androidterminal.core.domain.model.batch.Batch
import com.ationet.androidterminal.core.domain.model.batch.LoyaltyBatch
import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import com.ationet.androidterminal.core.domain.use_case.batch.GetLastOpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.batch.OpenBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.configuration.ConfigurationUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.GetLastOpenLoyaltyBatchUseCase
import com.ationet.androidterminal.core.domain.use_case.loyalty_batch.OpenLoyaltyBatchUseCase
import com.ationet.androidterminal.core.navigation.FleetGraph
import com.ationet.androidterminal.core.navigation.LoyaltyGraph
import com.ationet.androidterminal.core.navigation.MaintenanceGraph
import com.ationet.androidterminal.core.navigation.TaskGraph
import com.ationet.androidterminal.core.util.FileExportManager
import com.ationet.androidterminal.maintenance.home.HomeDestination
import com.ationet.androidterminal.ui.theme.AATColorScheme
import com.ationet.androidterminal.ui.theme.AATIconScheme
import com.ationet.androidterminal.ui.util.IconScheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    configurationUseCase: ConfigurationUseCase,
    private val openBatchUseCase: OpenBatchUseCase,
    private val openLoyaltyBatchUseCase: OpenLoyaltyBatchUseCase,
    private val getLastOpenBatchUseCase: GetLastOpenBatchUseCase,
    private val getLastOpenLoyaltyBatchUseCase: GetLastOpenLoyaltyBatchUseCase
) : ViewModel() {
    data class State(
        val colorScheme: ColorScheme,
        val iconScheme: IconScheme,
        val graph: FleetGraph,
        val isTerminalManagementEnabled: Boolean,
        val isConsumerCardEnabled: Boolean,
        val isLoyaltyEnabled: Boolean,
        val isGiftCardEnabled: Boolean,
        val options: List<HomeDestination>,
        val bottomOptions: List<Any>
    )

    val state: StateFlow<State> get() = _state.asStateFlow()
    private val _state: MutableStateFlow<State>

    init {
        val configuration = configurationUseCase.getConfiguration.invoke()
        setLanguage(configuration.language)

        val controllerGraph = getControllerGraph(configuration.controllerType)

        val initialState = State(
            colorScheme = AATColorScheme,
            iconScheme = AATIconScheme,
            graph = controllerGraph,
            isTerminalManagementEnabled = configuration.terminalManagement.terminalManagementEnabled,
            isConsumerCardEnabled = configuration.ationet.promptConsumerCard,
            isGiftCardEnabled = configuration.ationet.promptGiftCard,
            isLoyaltyEnabled = configuration.ationet.promptLoyalty,
            options = buildList {
                add(HomeDestination.ChangePasswordSupervisor)
                if (controllerGraph == FleetGraph.StandAlone || controllerGraph == FleetGraph.Fusion) {
                    add(HomeDestination.CreateOrEditProducts.Home)
                }
                add(HomeDestination.SynchronizeNow)
                add(HomeDestination.SendLogs)
                add(HomeDestination.Settings)
            },
            bottomOptions = listOf(
                controllerGraph,
                TaskGraph,
                LoyaltyGraph,
                MaintenanceGraph,
            )
        )

        _state = MutableStateFlow(initialState)

        initBatch()
    }

    private fun getControllerGraph(controllerType: Configuration.ControllerType): FleetGraph {
        return when (controllerType) {
            Configuration.ControllerType.STAND_ALONE -> FleetGraph.StandAlone
            Configuration.ControllerType.FUSION -> FleetGraph.Fusion
            Configuration.ControllerType.NANO_CPI -> TODO()
            Configuration.ControllerType.CONTROL_GAS -> TODO()
            Configuration.ControllerType.CPI_4G -> TODO()
            Configuration.ControllerType.COMMANDER -> TODO()
        }
    }

    private fun setLanguage(language: Configuration.LanguageType) {
        val locale = when (language) {
            Configuration.LanguageType.ES -> "es"
            Configuration.LanguageType.EN -> "en"
        }

        viewModelScope.launch {
            // SET LOCALE
            Log.i(TAG, "SETTING LOCALE TO $language")

            withContext(Dispatchers.Main) {
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(locale)
                )
            }
        }
    }

    fun updateColorScheme(color: Color) {
        _state.update {
            it.copy(
                colorScheme = AATColorScheme.copy(
                    primary = color
                )
            )
        }
    }

    fun updateIconScheme() {
        _state.update {
            it.copy(
                iconScheme = AATIconScheme.copy(
                    preAuthorization = R.drawable.hashtag,
                    receipt = R.drawable.hashtag
                )
            )
        }
    }

    //region Batch
    private fun initBatch() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getLastOpenBatchUseCase()
            if (result == null) {
                val currentInstant = Clock.System.now()
                openBatchUseCase(
                    Batch(
                        transactionDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                        state = Batch.State.OPEN
                    )
                )
            }
            val resultLoyalty = getLastOpenLoyaltyBatchUseCase()
            if (resultLoyalty == null) {
                val currentInstant = Clock.System.now()
                openLoyaltyBatchUseCase(
                    LoyaltyBatch(
                        transactionDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()),
                        state = LoyaltyBatch.State.OPEN
                    )
                )
            }
        }
    }
    //endregion

    companion object {
        private const val TAG = "MainActivityVM"
    }
}