package com.ationet.androidterminal.core.domain.model.configuration

import com.ationet.androidterminal.maintenance.settings.presentation.CodeItem

enum class CurrencyCode(
    override val code: String,
    override val description: String,
    override val symbol: String
) :
    CodeItem {
    COR("COR", "Córdoba Nicaragua", "C$"),
    ZMW("ZMW", "Zambian Kwacha", "ZK"),
    NAD("NAD", "Namibian Dollar", "N$"),
    BWP("BWP", "Botswana Pula", "P"),
    ANG("ANG", "Florin Antillano", "ƒ"),
    CLP("CLP", "Peso Chileno", "$"),
    KYD("KYD", "Cayman Dollar", "$"),
    BSD("BSD", "Dólar Bahameño", "$"),
    PEN("PEN", "Sol", "S/"),
    SRD("SRD", "Surinamese Dollar", "$"),
    ZAR("ZAR", "South African Rand", "R"),
    BZD("BZD", "Dólar Beliceno", "BZ$"),
    COP("COP", "Peso Colombiano", "$"),
    BMD("BMD", "Dólar Bermudeño", "$"),
    GTQ("GTQ", "Quetzal", "Q"),
    EUR("EUR", "Euro", "€"),
    BBD("BBD", "Dólar Barbados", "$"),
    BOB("BOB", "Bolivian Boliviano", "Bs"),
    GYD("GYD", "Dólar Guyanes", "$"),
    HNL("HNL", "Lempira", "L"),
    XCD("XCD", "Eastern Caribbean Dollar", "$"),
    AWG("AWG", "Florin Aruba", "ƒ"),
    CAD("CAD", "Dólar Canadiense", "$"),
    MXN("MXN", "Peso Mexicano", "$"),
    DOP("DOP", "Peso Dominicano", "$"),
    TTD("TTD", "Dólar Trinitense", "$"),
    ARS("ARS", "Peso Argentino", "$"),
    JMD("JMD", "Jamaican Dollar", "J$"),
    USD("USD", "Dólar Estados Unidos", "$"),
    UYU("UYU", "Uruguayan Peso", "$" + "U")
}