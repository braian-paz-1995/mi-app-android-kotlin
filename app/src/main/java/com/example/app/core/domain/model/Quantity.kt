package com.ationet.androidterminal.core.domain.model

data class Quantity(
    val inputType: InputType = InputType.Quantity,
    val value: Double = 0.0,
)
