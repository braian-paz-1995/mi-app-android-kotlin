package com.ationet.androidterminal.hal.card_reader.config

data class Application(
    val fields: List<Field>
)
data class ApplicationData(
    val AID: String,
    val KernelID: String,
    val common: Common,
    val scheme: Scheme
)
data class Common(
    val fields: List<Field>
)
data class Drl(
    val name: String,
    val tag: String,
    val values: List<Value>
)
data class EmvContactConfigTlv(
    val applications: List<Application>,
    val terminal: Terminal
)
data class EmvCtlsConfigTlv(
    val ApplicationData: List<ApplicationData>,
    val terminal: Terminal
)
data class Field(
    val name: String,
    val tag: String,
    val type: String,
    val value: String
)
data class Scheme(
    val drl: Drl,
    val fields: List<Field>,
    val name: String
)
data class Terminal(
    val fields: List<Field>
)
data class Value(
    val fields: List<Field>
)