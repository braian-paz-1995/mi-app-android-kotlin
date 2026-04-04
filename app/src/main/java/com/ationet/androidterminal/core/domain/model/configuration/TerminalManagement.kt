package com.ationet.androidterminal.core.domain.model.configuration

import com.ationet.androidterminal.R
import com.ationet.androidterminal.core.domain.model.configuration.Configuration.Companion.Defaults
import kotlin.time.Duration

data class TerminalManagement(
    val terminalManagementEnabled: Boolean = Defaults.DEFAULT_TERMINAL_MANAGEMENT_ENABLED,
    val terminalManagementUrl: String = Defaults.DEFAULT_TERMINAL_MANAGEMENT_URL,
    val pollInterval: Duration = Defaults.DEFAULT_POLL_INTERVAL,
    val sendReportAutomatically: Boolean = Defaults.DEFAULT_SEND_REPORT_AUTOMATICALLY,
    val levelReport: LevelReport = Defaults.DEFAULT_LEVEL_REPORT,
    val fileSize: Int = Defaults.DEFAULT_FILE_SIZE,
) {
    enum class LevelReport(val displayName: Int) {
        VERY_DETAILED(R.string.very_detailed),
        IMPORTANT_INFORMATION_ONLY(R.string.important_information_only),
        WARNING_AND_ERRORS(R.string.warnings_and_errors);
    }
}