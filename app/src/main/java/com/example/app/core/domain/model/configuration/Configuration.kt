package com.ationet.androidterminal.core.domain.model.configuration

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class Configuration(
    val language: LanguageType = Defaults.DEFAULT_LANGUAGE,
    val controllerType: ControllerType = Defaults.DEFAULT_CONTROLLER_TYPE,
    val selectedModule: ModuleType = Defaults.DEFAULT_MODULE_TYPE,
    val fusion: Fusion = Fusion(),
    val ationet: Ationet = Ationet(),
    val terminalManagement: TerminalManagement = TerminalManagement(),
    val site: Site = Site(),
    val ticket: Ticket = Ticket(),
    val editProductInfo: Boolean = Defaults.DEFAULT_EDIT_PRODUCT,
    val fuelMeasureUnit: String = Defaults.DEFAULT_FUEL_MEASURE_UNIT,
    val gncMeasureUnit: String = Defaults.DEFAULT_GNC_MEASURE_UNIT,
    val currencyFormat: String = Defaults.DEFAULT_CURRENCY_FORMAT,
    val currencyCode: String = Defaults.DEFAULT_CURRENCY_CODE,
    val supervisorPassword: String = Defaults.DEFAULT_SUPERVISOR_PASSWORD,
    val tsn: String = Defaults.DEFAULT_TSN,
    val transactionExpirationDays: Int = Defaults.DEFAULT_TRANSACTION_EXPIRATION_DAYS
) {

    enum class LanguageType(val message: String) {
        ES("Español"),
        EN("English")
    }

    enum class ModuleType {
        LOCAL_AGENT,
        CONSUMER_CARD,
        GIFT_CARD,
        LOYALTY,
        FLEET,
        OTHER;
        fun displayName(language: LanguageType): String {
            return when (this) {
                FLEET -> if (language == LanguageType.ES) "Flota" else "Fleet"
                LOCAL_AGENT -> if (language == LanguageType.ES) "Local Agent" else "Local Agent"
                CONSUMER_CARD -> if (language == LanguageType.ES) "Consumer Card" else "Consumer Card"
                GIFT_CARD -> if (language == LanguageType.ES) "Tarjeta de Regalo" else "Gift Card"
                LOYALTY -> if (language == LanguageType.ES) "Fidelidad" else "Loyalty"
                OTHER -> if (language == LanguageType.ES) "Otro" else "Other"
            }
        }
    }

    enum class ControllerType {
        STAND_ALONE,
        FUSION,
        NANO_CPI,
        CONTROL_GAS,
        CPI_4G,
        COMMANDER
    }

    companion object {
        object Defaults {
            val DEFAULT_LANGUAGE: LanguageType = LanguageType.EN
            val DEFAULT_CONTROLLER_TYPE: ControllerType = ControllerType.STAND_ALONE
            val DEFAULT_MODULE_TYPE: ModuleType = ModuleType.FLEET

            const val DEFAULT_FUSION_IP: String = ""
            const val DEFAULT_FUSION_PORT: String = ""
            const val DEFAULT_FUSION_PAYMENT_TYPE_CODE: String = ""
            const val DEFAULT_FUSION_AUTHORIZATION_CODE_TAG: String = "\$AUC"
            const val DEFAULT_FUSION_PAYMENT_METHOD: Boolean = true
            const val DEFAULT_ENABLE_FINALIZATION_VARIANCE: Boolean = false


            const val DEFAULT_NATIVE_URL: String = "https://native.ationet.com/"
            const val DEFAULT_VISION_URL: String =
                "https://ationetvisionapi-test.azurewebsites.net/api/Recognitions/plate"
            const val DEFAULT_USERNAME_CC: String = ""
            const val DEFAULT_PASSWORD_CC: String = ""
            const val DEFAULT_USERNAME_GC: String = ""
            const val DEFAULT_PASSWORD_GC: String = ""
            const val DEFAULT_USERNAME_LOYALTY: String = ""
            const val DEFAULT_PASSWORD_LOYALTY: String = ""
            const val DEFAULT_TERMINAL_ID: String = ""
            const val DEFAULT_PROMPT_AMOUNT_TRANSACTION: Boolean = true
            const val DEFAULT_PROMPTS_DEFAULT: Boolean = false
            const val DEFAULT_PROMPT_CONSUMER_CARD: Boolean = false
            const val DEFAULT_PROMPT_LOYALTY: Boolean = false
            const val DEFAULT_PROMPT_GIFT_CARD: Boolean = false
            const val DEFAULT_PROMPTS_PAYMENT_METHOD_ENABLED: Boolean = true
            const val DEFAULT_PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED: Boolean = true
            const val DEFAULT_LOCAL_AGENT: Boolean = false
            const val DEFAULT_LOCAL_AGENT_IP = ""
            const val DEFAULT_LOCAL_AGENT_PORT = "33173"

            const val DEFAULT_TERMINAL_MANAGEMENT_ENABLED: Boolean = false
            const val DEFAULT_TERMINAL_MANAGEMENT_URL: String =
                "https://terminalsmanagementapi.ationet.com"
            val DEFAULT_POLL_INTERVAL: Duration = 15.minutes
            const val DEFAULT_SEND_REPORT_AUTOMATICALLY: Boolean = false
            val DEFAULT_LEVEL_REPORT: TerminalManagement.LevelReport =
                TerminalManagement.LevelReport.VERY_DETAILED
            const val DEFAULT_FILE_SIZE: Int = 4
            const val DEFAULT_SITE_CODE: String = ""
            const val DEFAULT_SITE_NAME: String = ""
            const val DEFAULT_SITE_ADDRESS: String = ""
            const val DEFAULT_SITE_CUIT: String = ""
            const val DEFAULT_DRIVER_IDENTIFICATION: Boolean = false
            const val DEFAULT_VEHICLE_IDENTIFICATION: Boolean = false
            const val DEFAULT_ATIONET_VISION_VEHICLE_ID: Boolean = false
            const val DEFAULT_COMPANY_NAME: Boolean = false
            const val DEFAULT_MERCHANT_ID: Boolean = false
            const val DEFAULT_PRIMARY_IDENTIFICATION: Boolean = false
            const val DEFAULT_SECONDARY_IDENTIFICATION: Boolean = false
            const val DEFAULT_TRANSACTION_DETAILS: Boolean = false
            const val DEFAULT_TICKET_TITLE: String = ""
            const val DEFAULT_TICKET_SUBTITLE: String = ""
            const val DEFAULT_TICKET_FOOTER: String = ""
            const val DEFAULT_TICKET_BOTTOM_NOTE: String = ""
            const val DEFAULT_INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE: Boolean = false
            const val DEFAULT_TRANSACTION_DETAILS_IN_COLUMNS: Boolean = true

            const val DEFAULT_EDIT_PRODUCT: Boolean = true
            val DEFAULT_FUEL_MEASURE_UNIT: String = TypeCode.LITRE.code
            val DEFAULT_GNC_MEASURE_UNIT: String = TypeCode.CUBIC_METRE.code
            val DEFAULT_CURRENCY_FORMAT: String = CurrencyCode.ARS.symbol
            val DEFAULT_CURRENCY_CODE: String = CurrencyCode.ARS.code
            const val DEFAULT_SUPERVISOR_PASSWORD: String = "Ationet@1"
            const val DEFAULT_TSN: String = ""
            const val DEFAULT_TRANSACTION_EXPIRATION_DAYS: Int = 30
        }

        object Keys {
            const val LANGUAGE_KEY = "KEY_LANGUAGE"
            const val CONTROLLER_TYPE_KEY = "KEY_CONTROLLER_TYPE"
            const val SELECTED_MODULE_KEY = "KEY_SELECTED_MODULE"
            const val FUSION_IP_KEY = "KEY_FUSION_IP"
            const val FUSION_PORT_KEY = "KEY_FUSION_PORT"
            const val FUSION_PAYMENT_TYPE_CODE_KEY = "KEY_FUSION_PAYMENT_TYPE_CODE"
            const val FUSION_PAYMENT_METHOD_KEY = "KEY_FUSION_PAYMENT_METHOD"
            const val ENABLE_FINALIZATION_VARIANCE_KEY = "KEY_ENABLE_FINALIZATION_VARIANCE"
            const val FUSION_AUTHORIZATION_CODE_TAG_KEY = "KEY_FUSION_AUTHORIZATION_CODE_TAG"
            const val NATIVE_URL_KEY = "KEY_NATIVE_URL"
            const val VISION_URL_KEY = "KEY_VISION_URL"
            const val CC_USERNAME_KEY = "KEY_USERNAME_CC"
            const val CC_PASSWORD_KEY = "KEY_PASSWORD_CC"
            const val GC_USERNAME_KEY = "KEY_USERNAME_GC"
            const val GC_PASSWORD_KEY = "KEY_PASSWORD_GC"
            const val LOYALTY_USERNAME_KEY = "KEY_USERNAME_LOYALTY"
            const val LOYALTY_PASSWORD_KEY = "KEY_PASSWORD_LOYALTY"
            const val TERMINAL_ID_KEY = "KEY_TERMINAL_ID"
            const val PROMPT_AMOUNT_TRANSACTION_KEY = "KEY_PROMPT_AMOUNT_TRANSACTION"
            const val PROMPT_CONSUMERCARD_KEY = "KEY_CONSUMERCARD"
            const val PROMPT_LOYALTY_KEY = "KEY_LOYALTY"
            const val PROMPT_GIFTCARD_KEY = "KEY_GIFTCARD"
            const val PROMPTS_PAYMENT_METHOD_ENABLED = "KEY_PROMPTS_PAYMENT_METHOD_ENABLED"
            const val PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED = "KEY_PROMPTS_LOYALTY_PAYMENT_METHOD_ENABLED"
            const val PROMPTS_DEFAULT_KEY = "KEY_PROMPTS_DEFAULT"
            const val PROMPT_ATTENDANT_IDENTIFICATION_KEY = "KEY_PROMPT_ATTENDANT_IDENTIFICATION"
            const val PROMPT_DRIVER_IDENTIFICATION_KEY = "KEY_PROMPT_DRIVER_IDENTIFICATION"
            const val PROMPT_VEHICLE_IDENTIFICATION_KEY = "KEY_PROMPT_VEHICLE_IDENTIFICATION"
            const val PROMPT_ODOMETER_KEY = "KEY_PROMPT_ODOMETER"
            const val PROMPT_ENGINE_HOURS_KEY = "KEY_PROMPT_ENGINE_HOURS"
            const val PROMPT_TRAILER_KEY = "KEY_PROMPT_TRAILER"
            const val PROMPT_MISCELLANEOUS_KEY = "KEY_PROMPT_MISCELLANEOUS"
            const val PROMPT_TRUCK_UNIT_KEY = "KEY_PROMPT_TRUCK_UNIT"
            const val PROMPT_SECONDARY_TRACK_KEY = "KEY_PROMPT_SECONDARY_TRACK"
            const val PROMPT_PRIMARY_PIN_KEY = "KEY_PROMPT_PRIMARY_PIN"
            const val PROMPT_SECONDARY_PIN_KEY = "KEY_PROMPT_SECONDARY_PIN"
            const val LOCAL_AGENT_KEY = "KEY_LOCAL_AGENT"
            const val LOCAL_AGENT_IP_KEY = "KEY_LOCAL_AGENT_IP"
            const val LOCAL_AGENT_PORT_KEY = "KEY_LOCAL_AGENT_PORT"
            const val TERMINAL_MANAGEMENT_ENABLED_KEY = "KEY_TERMINAL_MANAGEMENT_ENABLED"
            const val TERMINAL_MANAGEMENT_URL_KEY = "KEY_TERMINAL_MANAGEMENT_URL"
            const val POLL_INTERVAL_KEY = "KEY_POOL_INTERVAL"
            const val SEND_REPORT_AUTOMATICALLY_KEY = "KEY_SEND_REPORT_AUTOMATICALLY"
            const val LEVEL_REPORT_KEY = "KEY_LEVEL_REPORT"

            /*const val FILE_SIZE_KEY = "KEY_FILE_SIZE"*/
            const val SITE_CODE_KEY = "KEY_SITE_CODE"
            const val SITE_NAME_KEY = "KEY_SITE_NAME"
            const val SITE_ADDRESS_KEY = "KEY_SITE_ADDRESS"
            const val SITE_CUIT_KEY = "KEY_SITE_CUIT"
            const val DRIVER_IDENTIFICATION_KEY = "KEY_DRIVER_IDENTIFICATION"
            const val VEHICLE_IDENTIFICATION_KEY = "KEY_VEHICLE_IDENTIFICATION"
            const val ATIONET_VISION_VEHICLE_ID_KEY = "KEY_ATIONET_VISION_VEHICLE_ID_"
            const val COMPANY_NAME_KEY = "KEY_COMPANY_NAME"
            const val MERCHANT_ID_KEY = "KEY_MERCHANT_ID"
            const val PRIMARY_IDENTIFICATION_KEY = "KEY_PRIMARY_IDENTIFICATION"
            const val SECONDARY_IDENTIFICATION_KEY = "KEY_SECONDARY_IDENTIFICATION"
            const val TRANSACTION_DETAILS_KEY = "KEY_TRANSACTION_DETAILS"
            const val TICKET_TITLE_KEY = "KEY_TICKET_TITLE"
            const val TICKET_SUBTITLE_KEY = "KEY_TICKET_SUBTITLE"
            const val TICKET_FOOTER_KEY = "KEY_TICKET_FOOTER"
            const val TICKET_BOTTOM_NOTE_KEY = "KEY_TICKET_BOTTOM_NOTE"
            const val INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE_KEY =
                "KEY_INVOICE_NUMBER_INSTEAD_OF_AUTHORIZATION_CODE"
            const val PRODUCT_DETAILS_IN_COLUMNS_KEY = "KEY_PRODUCT_DETAILS_IN_COLUMNS"
            const val EDIT_PRODUCT_KEY = "KEY_EDIT_PRODUCT"
            const val FUEL_MEASURE_UNIT_KEY = "KEY_FUEL_MEASURE_UNIT"
            const val GNC_MEASURE_UNIT_KEY = "KEY_GNC_MEASURE_UNIT"
            const val CURRENCY_FORMAT_KEY = "KEY_CURRENCY_FORMAT"
            const val CURRENCY_CODE_KEY = "KEY_CURRENCY_CODE"
            const val SUPERVISOR_PASSWORD_KEY = "KEY_SUPERVISOR_PASSWORD"
            const val TSN_KEY_DEFAULT = "KEY_TSN_DEFAULT"
            const val TRANSACTION_EXPIRATION_DAYS_KEY = "KEY_TRANSACTION_EXPIRATION_DAYS"

        }
    }
}