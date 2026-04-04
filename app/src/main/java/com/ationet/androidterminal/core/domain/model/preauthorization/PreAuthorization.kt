package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.domain.model.configuration.Configuration
import kotlinx.datetime.LocalDateTime

data class PreAuthorization(
    val id: Int = 0,
    val createAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val identification: Identification,
    val authorization: Authorization,
    val originalData : OriginalData,
    val customerData: CustomerData,
    val batchId: Int,
    val controllerType: Configuration.ControllerType
)