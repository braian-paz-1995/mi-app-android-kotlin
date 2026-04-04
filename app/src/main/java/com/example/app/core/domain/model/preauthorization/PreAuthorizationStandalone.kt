package com.ationet.androidterminal.core.domain.model.preauthorization

import com.ationet.androidterminal.core.domain.model.configuration.Configuration

data class PreAuthorizationStandalone(
    val preAuthorization: PreAuthorization,
    val productId: Int = 0,
    val productCode: String,
    val productName: String,
    val productUnitPrice: Double,
    val inputType: InputType,
    val controllerType: Configuration.ControllerType
)