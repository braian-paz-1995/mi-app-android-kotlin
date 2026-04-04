package com.ationet.androidterminal.core.domain.model.configuration

import com.ationet.androidterminal.maintenance.settings.presentation.CodeItem

enum class TypeCode(override val code: String, override val description: String) : CodeItem {
    US_GALLON("usgal", "USA Gallon"),
    UK_GALLON("ukgal", "UK Gallon"),
    LITRE("L", "Litre"),
    CUBIC_METRE("m³", "Cubic metre"),
    KILOGRAM("kg", "Kilogram");

    override val symbol: String? = null
}